package com.discut.manga.service.saver.download.model

import android.util.Log
import com.discut.manga.App
import com.discut.manga.data.toSChapter
import com.discut.manga.service.saver.download.DownloadFileSystem
import com.discut.manga.service.saver.download.DownloadProvider
import com.discut.manga.service.saver.download.instance
import com.discut.manga.util.get
import com.discut.manga.util.launchIO
import com.discut.manga.util.withIOContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import manga.core.preference.DownloadPreference
import manga.core.preference.PreferenceManager
import manga.source.HttpSource
import manga.source.domain.Page
import manga.source.extensions.toInputStream


/**
 * in fact, it only contains one Source
 */
class DownloadScope {

    private val downloadPreference = PreferenceManager.get<DownloadPreference>()

    lateinit var source: HttpSource

    private val _queueState: MutableStateFlow<DownloaderQueue> =
        MutableStateFlow(DownloaderQueue())

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var downloadMainJob: Job? = null

    private val _activeDownloaderJobsFlow: MutableStateFlow<Map<Downloader, Job>> =
        MutableStateFlow(emptyMap())
    private val activeDownloaderJobs
        get() = _activeDownloaderJobsFlow.value

    private val downloadProvider = DownloadProvider.instance

    val isRunning get() = downloadMainJob?.isActive ?: false
    val queueState = _queueState.asStateFlow()

    val scopeTag
        get() = "${source.id}-${source.name}"

    companion object {
        const val CHANNEL_ID = 100
        const val CHANNEL_NAME = "com.discut.manga.service.saver.download.model.DownloadScope"

        const val MAX_DOWNLOADS = 1

        const val TAG = "DownloadScope"
    }

    private fun MutableStateFlow<DownloaderQueue>.updateBySorted(function: (DownloaderQueue) -> DownloaderQueue) {
        update { it ->
            val downloaderQueue = function(it)
            downloaderQueue.copy(
                queue = downloaderQueue.queue.sortedBy { it.status.getNumber() }
            )
        }
    }

    /**
     * add new downloader
     */
    fun add(downloader: Downloader) {
        synchronized(_queueState) {
            _queueState.value.queue.find { it.download.id == downloader.download.id }
                ?: _queueState.updateBySorted {
                    it.copy(
                        queue = it.queue + downloader
                    )
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun bootDownloadMainJob(): Boolean {
        Log.d("DownloadScope", "try boot download main job, isRunning is $isRunning")
        if (isRunning || _queueState.value.queue.isEmpty()) {
            return false
        }
        downloadMainJob = scope.launch {
            val activeDownloadsFlow = queueState
                .transformLatest { downloaderQueue ->
                    while (true) {
                        Log.i("DownloadScope", "downloaderQueue is ${downloaderQueue.queue.size}")
                        Log.i(
                            "DownloadScope",
                            "${downloaderQueue.queue.map { "${it.chapter.name}:${it.status} \n" }}"
                        )
                        val sequence = downloaderQueue.queue.asSequence()
                            .filter { it.status == Downloader.DownloadState.Waiting }
                            .sortedBy { downloader -> downloader.download.order }
                            .take(MAX_DOWNLOADS - activeDownloaderJobs.size)
                            .toList()
                        emit(sequence)
                        delay(500)
                        if (downloaderQueue.queue.filter {
                                it.status == Downloader.DownloadState.Waiting ||
                                        it.status == Downloader.DownloadState.Downloading
                            }.isEmpty()) {
                            DownloadWorker.stopScope(App.instance, this@DownloadScope)
                        }
                    }
                }.distinctUntilChanged()

            supervisorScope {
                activeDownloadsFlow.collectLatest { downloaders ->
                    _activeDownloaderJobsFlow.update {
                        it + downloaders.associateWith { launchDownloaderJob(it) }
                    }
                }
            }
        }
        return true
    }

    private fun CoroutineScope.launchDownloaderJob(downloader: Downloader): Job = launchIO {
        try {
            if (!downloader.canDownload()) {
                throw IllegalStateException("${downloader.getTag()}: status is ${downloader.status}, can't download again.")
            }
            handleDownloaderState(downloader)
            handleDownloaderProgress(downloader)

            downloader.status = Downloader.DownloadState.Downloading

            executeDownload(downloader)
        } catch (e: CancellationException) {
            downloader.status = Downloader.DownloadState.InQueue
        } catch (e: Throwable) {
            downloader.status =
                Downloader.DownloadState.Error(e, "${downloader.getTag()}: ${e.message}")
        }
    }

    /**
     * Only status Downloading will be handled
     */
    private fun CoroutineScope.handleDownloaderState(downloader: Downloader) =
        launchIO {
            val statusHandler = StatusHandler.create(downloader)
            downloader.statusFlow.collect { downloadState ->
                when (downloadState) {
                    Downloader.DownloadState.InQueue -> {
                        _activeDownloaderJobsFlow.tryCancelJob(downloader)
                        statusHandler.onInQueue()
                    }

                    is Downloader.DownloadState.Error -> {
                        _activeDownloaderJobsFlow.tryCancelJob(downloader)
                        statusHandler.onError()
                        /* downloader.download = downloader.download.copy(
                             status = if (downloadState is Downloader.DownloadState.Error) {
                                 DownloadState.Error
                             } else {
                                 DownloadState.InQueue
                             },
                             remarks = (downloadState as? Downloader.DownloadState.Error)?.msg
                                 ?: ""
                         )
                         downloadProvider.updateOrInsertDownload(
                             downloader.download
                         )*/
                        Log.i("DownloadScope", "${downloader.getTag()}: Error or InQueue")
                    }

                    Downloader.DownloadState.Downloaded -> {
                        _activeDownloaderJobsFlow.tryCancelJob(downloader)
                        _queueState.update { downloaderQueue ->
                            downloaderQueue.copy(
                                queue = downloaderQueue.queue.filter { it.download.id != downloader.download.id }
                            )
                        }
                        statusHandler.onDownloaded()
                        /*                        downloader.download = downloader.download.copy(
                                                    status = DownloadState.Completed,
                                                )
                                                downloadProvider.updateOrInsertDownload(
                                                    downloader.download
                                                )*/
                        Log.i("DownloadScope", "${downloader.getTag()}: Downloaded")
                    }

                    Downloader.DownloadState.Downloading -> {
                        statusHandler.onDownloading()
                        /*                        downloader.download = downloader.download.copy(
                                                    status = DownloadState.Downloading,
                                                )
                                                downloadProvider.updateOrInsertDownload(
                                                    downloader.download
                                                )*/
                        Log.i("DownloadScope", "${downloader.getTag()}: Downloading")
                    }


                    Downloader.DownloadState.Waiting -> {
                        statusHandler.onWaiting()
                        /*                        downloader.download = downloader.download.copy(
                                                    status = DownloadState.Waiting,
                                                )
                                                downloadProvider.updateOrInsertDownload(
                                                    downloader.download
                                                )*/
                        Log.i("DownloadScope", "${downloader.getTag()}: Waiting")
                    }
                }
            }
        }

    private fun CoroutineScope.handleDownloaderProgress(downloader: Downloader) =
        launchIO {
            downloader.progressFlow.collect {
                //Log.i("DownloadScope", "${downloader.getTag()}: progress is $it")
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun executeDownload(downloader: Downloader) {
        val pages = if (downloader.pages == null || downloader.pages?.isEmpty() == true) {
            source.getPageList(downloader.chapter.toSChapter())
        } else {
            downloader.pages!!
        }.sortedBy { it.index }

        if (pages.isEmpty()) {
            throw IllegalStateException("${downloader.getTag()}: pages is empty.")
        }

        pages.asFlow()
            .filter { it.status != Page.State.READY }
            .flatMapMerge(concurrency = 2) {
                flow {
                    if (downloader.status != Downloader.DownloadState.Downloading) {
                        throw CancellationException("${downloader.getTag()}: status is ${downloader.status}")
                    }
                    when (it.status) {
                        Page.State.DOWNLOAD_IMAGE, Page.State.READY -> emit(it)
                        Page.State.QUEUE, Page.State.LOAD_PAGE, Page.State.ERROR -> {
                            delay(downloadPreference.getDownloadInterval().toLong())
                            if (it.imageUrl.isNullOrEmpty()) {
                                it.status = Page.State.LOAD_PAGE
                                try {
                                    it.imageUrl = source.getImageUrl(it)
                                } catch (e: Exception) {
                                    it.status = Page.State.ERROR
                                }
                            }
                            withIOContext { downloadImage(downloader, it) }
                        }
                    }
                    emit(it)
                }.flowOn(Dispatchers.IO)
            }
            .buffer()
            .collect { it ->
                // 每次下载完成一个图片后在数据库保存信息
                // 数据库升级请保持线程安全，该下载是异步多线程的
                if (it.status == Page.State.READY) {
                    val newQueue = downloader.download.queue.let { queue ->
                        if (queue.contains(it.index.toLong())) {
                            queue.toMutableList().apply {
                                remove(it.index.toLong())
                            }
                        } else {
                            queue
                        }
                    }
                    val newDownloaded = downloader.download.downloaded.let { downloaded ->
                        if (!downloaded.contains(it.index.toLong())) {
                            downloaded.toMutableList().apply {
                                add(it.index.toLong())
                            }
                        } else {
                            downloaded
                        }
                    }
                    downloader.download = downloader.download.copy(
                        queue = newQueue,
                        downloaded = newDownloaded
                    )
                    downloadProvider.updateOrInsertDownload(
                        downloader.download
                    )
                    if (newQueue.isEmpty()) {
                        downloader.status = Downloader.DownloadState.Downloaded
                    }
                    //_activeDownloaderJobsFlow.rinseMap()
                }
            }
    }

    private suspend fun downloadImage(downloader: Downloader, page: Page) {
        page.status = Page.State.DOWNLOAD_IMAGE
        val image = downloader.source.getImage(page).toInputStream()
        DownloadFileSystem(downloadPreference.getDownloadDirectory(App.instance)).apply {
            savePage(downloader, page, image)
        }
    }

    private fun MutableMap<Downloader, Job>.tryCancelJob(downloader: Downloader) {
        this[downloader]?.cancel()
        this.remove(downloader)
    }

    private fun MutableStateFlow<Map<Downloader, Job>>.tryCancelJob(downloader: Downloader) {
        this.value[downloader]?.cancel()
        update {
            val map = value.toMutableMap().apply {
                remove(downloader)
            }
            map
        }
    }

    private fun MutableStateFlow<Map<Downloader, Job>>.rinseMap() {
        update {
            it.filter { entry -> entry.key.status != Downloader.DownloadState.Downloaded }
        }
    }


    suspend fun startAll() {
        withIOContext {
            _queueState.update { downloaderQueue ->
                downloaderQueue.copy(
                    queue = downloaderQueue.queue.filter { it.status != Downloader.DownloadState.Downloaded }
                        .onEach {
                            if (it.status == Downloader.DownloadState.InQueue ||
                                it.status is Downloader.DownloadState.Error
                            ) {
                                it.status = Downloader.DownloadState.Waiting
                                StatusHandler.create(it).onWaiting()
                            }
                        }.sortedBy { it.status.getNumber() }
                )
            }
        }
        // bootDownloadMainJob()
    }

    suspend fun start(downloader: Downloader) {
        queueState.value.queue.find { it.download.id == downloader.download.id }?.status =
            Downloader.DownloadState.Waiting
        withIOContext {
            StatusHandler.create(downloader).onWaiting()
        }
        _queueState.updateBySorted { downloaderQueue ->
            downloaderQueue.copy(
                queue = downloaderQueue.queue
                    .sortedBy { it.download.order }
                    .sortedBy { it.status.getNumber() }
            )
        }
    }

    fun stop() {
        pauseAll()
        downloadMainJob?.cancel()
        downloadMainJob = null
    }

    fun cancelAll() {
        pauseAll()
        _queueState.updateBySorted {
            DownloaderQueue()
        }
    }

    fun cancel(downloader: Downloader) {
        queueState.value.queue.find { it.download.id == downloader.download.id }?.status =
            Downloader.DownloadState.InQueue
        _queueState.updateBySorted { downloaderQueue ->
            downloaderQueue.copy(
                queue = downloaderQueue.queue.filter { it.download.id != downloader.download.id }
            )
        }
    }

    fun pauseAll() {
        val scope = CoroutineScope(Dispatchers.IO)
        _queueState.updateBySorted { it ->
            it.copy(
                queue = it.queue.filter { it.status == Downloader.DownloadState.Downloading || it.status == Downloader.DownloadState.Waiting }
                    .onEach {
                        if (it.status != Downloader.DownloadState.Downloading) {
                            scope.launchIO {
                                StatusHandler.create(it).onInQueue()
                            }
                        }
                        it.status = Downloader.DownloadState.InQueue
                    }
            )
        }
        activeDownloaderJobs.forEach { (_, job) ->
            job.cancel()
        }
        _activeDownloaderJobsFlow.update { emptyMap() }
    }

    suspend fun pause(downloader: Downloader) {
        val previousStatus = downloader.status
        queueState.value.queue.find { it.download.id == downloader.download.id }?.status =
            Downloader.DownloadState.InQueue

        if (previousStatus != Downloader.DownloadState.Downloading) {
            withIOContext {
                StatusHandler.create(downloader).onInQueue()
            }
        }
    }


    fun Downloader.getTag() = "${manga.title}->${chapter.name}(${source.name}id:${source.id})"

    private fun Sequence<Downloader>.filterAndSortNeedDownload(): Sequence<Downloader> =
        filter {
            when (it.status) {
                Downloader.DownloadState.Waiting,
                Downloader.DownloadState.Downloading,
                Downloader.DownloadState.InQueue -> true

                else -> false
            }
        }.sortedWith { l, r ->
            if (l.status is Downloader.DownloadState.Downloading) {
                0
            } else {
                1
            }
        }

    suspend fun updateOrder(orders: Map<Long, Double>) {
        // First update db
        withIOContext {
            downloadProvider.updateOrder(orders)
        }
        if (queueState.value.queue.isEmpty()) {
            return
        }
/*        // then update queue
        Log.i(
            "DownloadScreen", "sorted before ${
                queueState.value.queue.map {
                    "${it.chapter.name}:${it.download.order}  "
                }
            }"
        )
        val sorted = queueState.value.queue.map {
            it.apply {
                download = it.download.copy(order = orders[it.download.id] ?: it.download.order)
            }
        }.sortedBy { it.download.order }
        Log.i(
            "DownloadScreen", "sorted ${
                sorted.map {
                    "${it.chapter.name}:${it.download.order}  "
                }
            }"
        )
        *//*        var isContinue = false
                sorted.forEachIndexed { index, downloader ->
                    if (downloader.download.id != queueState.value.queue[index].download.id) {
                        isContinue = true
                    }
                }
                if (!isContinue) {
                    return
                }*//*
        _queueState.updateBySorted {
            it.copy(
                queue = sorted
            )
        }
        val needStopDownloader: MutableMap<Downloader, Job> = mutableMapOf()
        // update downloading status
        activeDownloaderJobs.keys.forEach {
            val index = queueState.value.queue.indexOf(it)
            if (index in 0..MAX_DOWNLOADS) {
                return@forEach
            }
            it.status = Downloader.DownloadState.Waiting
            needStopDownloader[it] = activeDownloaderJobs[it]!!
        }
        _activeDownloaderJobsFlow.update { it ->
            it.toMutableMap().apply {
                needStopDownloader.keys.forEach {
                    remove(it)
                }
            }
        }
        withIOContext {
            queueState.value.queue.forEach {
*//*            // update order from double to int
            it.download = it.download.copy(order = queueState.value.indexOf(it).toDouble())*//*
                // update db
                downloadProvider.updateOrInsertDownload(it.download)
            }
        }*/

    }
}
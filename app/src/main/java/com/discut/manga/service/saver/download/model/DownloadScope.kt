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
import discut.manga.data.download.DownloadState
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
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.supervisorScope
import manga.source.HttpSource
import manga.source.domain.Page
import manga.source.extensions.toInputStream
import manga.core.preference.DownloadPreference
import manga.core.preference.PreferenceManager


/**
 * in fact, it only contains one Source
 */
class DownloadScope {

    private val downloadPreference = PreferenceManager.get<DownloadPreference>()

    lateinit var source: HttpSource

    private val _queueState: MutableStateFlow<List<Downloader>> =
        MutableStateFlow(emptyList())

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var downloadMainJob: Job? = null

    private val _activeDownloaderJobsFlow: MutableStateFlow<Map<Downloader, Job>> =
        MutableStateFlow(emptyMap())
    private val activeDownloaderJobs
        get() = _activeDownloaderJobsFlow.value

    private val downloadProvider = DownloadProvider.instance

    val isRunning get() = downloadMainJob?.isActive ?: false
    val queueState get() = _queueState.asStateFlow()

    val scopeTag
        get() = "${source.id}-${source.name}"

    companion object {
        const val CHANNEL_ID = 100
        const val CHANNEL_NAME = "com.discut.manga.service.saver.download.model.DownloadScope"

        const val MAX_DOWNLOADS = 1

        const val TAG = "DownloadScope"
    }

    fun add(downloader: Downloader) {
        _queueState.value.find { it.download.id == downloader.download.id } ?: _queueState.update {
            it + downloader
        }
    }

    fun bootDownloadMainJob(): Boolean {
        if (isRunning || _queueState.value.isEmpty()) {
            return false
        }
        downloadMainJob = scope.launchIO {
            supervisorScope {
                _activeDownloaderJobsFlow.collect {
                    Log.i("DownloadScope", "activeDownloaderJobsFlow size is ${it.size}")
                    if (it.size >= MAX_DOWNLOADS) return@collect
                    synchronized(activeDownloaderJobs) {
                        try {
                            val downloader =
                                queueState.value.sortedBy { downloader -> downloader.download.order }
                                    .first { download -> download.status == Downloader.DownloadState.Waiting }
                            Log.i("DownloadScope", "${downloader.getTag()}: Start to download")
                            _activeDownloaderJobsFlow.update { downloaderMap ->
                                downloaderMap + (downloader to launchDownloaderJob(downloader))
                            }
                        } catch (e: Exception) {
                            Log.i("DownloadScope", "Queue is empty")
                            stop()
                        }
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
        } catch (e: Throwable) {
            downloader.status =
                Downloader.DownloadState.Error(e, "${downloader.getTag()}: ${e.message}")
        }
    }

    private fun CoroutineScope.handleDownloaderState(downloader: Downloader) =
        launchIO {
            downloader.statusFlow.collect {
                when (it) {
                    is Downloader.DownloadState.Error ->
                        _activeDownloaderJobsFlow.tryCancelJob(downloader)

                    Downloader.DownloadState.Downloaded, Downloader.DownloadState.InQueue ->
                        _activeDownloaderJobsFlow.tryCancelJob(downloader)

                    Downloader.DownloadState.Downloading -> {
                        Log.i("DownloadScope", "${downloader.getTag()}: Downloading")
                    }


                    Downloader.DownloadState.Waiting -> {
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
            .flatMapMerge(concurrency = 2) {
                flow {
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
                    val status = if (newQueue.isNotEmpty()) {
                        downloader.download.status
                    } else {
                        DownloadState.Completed
                    }
                    downloader.download = downloader.download.copy(
                        status = status,
                        queue = newQueue,
                        downloaded = newDownloaded
                    )
                    downloadProvider.updateOrInsertDownload(
                        downloader.download
                    )
                    if (status == DownloadState.Completed) {
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


    fun startAll() {
        val downloaderList =
            queueState.value.filter { it.status != Downloader.DownloadState.Downloaded }.onEach {
                if (it.status == Downloader.DownloadState.InQueue) {
                    it.status = Downloader.DownloadState.Waiting
                }
            }
        _queueState.update {
            downloaderList
        }

    }

    fun stop() {
        pauseAll()
        downloadMainJob?.cancel()
        downloadMainJob = null
    }

    fun pauseAll() {
        activeDownloaderJobs.forEach { (_, job) ->
            job.cancel()
        }
        _activeDownloaderJobsFlow.update { emptyMap() }
        _queueState.update { it ->
            it.filter { it.status == Downloader.DownloadState.Downloading || it.status == Downloader.DownloadState.Waiting }
                .onEach {
                    it.status = Downloader.DownloadState.InQueue
                }
            it
        }
    }

    fun cancel(downloader: Downloader) {
        downloader.status = Downloader.DownloadState.InQueue
        _queueState.update { downloaderList ->
            downloaderList.filter { it.download.id != downloader.download.id }
        }
    }

    fun pause(downloader: Downloader) {
        downloader.status = Downloader.DownloadState.InQueue
    }

    fun pause(downloadId: Long) {
        _queueState.value.find { it.download.id == downloadId }?.apply(::pause)
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

    suspend fun updateOrder() {
        // update order
        val sorted = queueState.value.sortedBy { it.download.order }
        var isContinue = false
        sorted.forEachIndexed { index, downloader ->
            if (downloader.download.id != queueState.value[index].download.id) {
                isContinue = true
            }
        }
        if (!isContinue) {
            return
        }
        _queueState.update {
            sorted
        }
        val needStopDownloader: MutableMap<Downloader, Job> = mutableMapOf()
        // update downloading status
        activeDownloaderJobs.keys.forEach {
            val index = queueState.value.indexOf(it)
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
            queueState.value.forEach {
/*            // update order from double to int
            it.download = it.download.copy(order = queueState.value.indexOf(it).toDouble())*/
                // update db
                downloadProvider.updateOrInsertDownload(it.download)
            }
        }

    }
}
package com.discut.manga.service.saver.download.model

import android.content.Context
import android.content.pm.ServiceInfo
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.discut.manga.data.toSChapter
import com.discut.manga.ui.util.NetworkState
import com.discut.manga.ui.util.activeNetworkState
import com.discut.manga.ui.util.networkStateFlow
import com.discut.manga.util.launchIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.supervisorScope
import managa.source.HttpSource
import manga.core.preference.DownloadPreference

class DownloadScope(
    context: Context, workerParams: WorkerParameters,
    private val downloadPreference: DownloadPreference,
) :
    CoroutineWorker(context, workerParams) {

    lateinit var source: HttpSource

    private val _queueState: MutableStateFlow<List<Downloader>> =
        MutableStateFlow(emptyList())

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var downloadJob: Job? = null

    private val activeDownloaderJobs: MutableMap<Downloader, Job> = mutableMapOf()

    val isRunning get() = downloadJob?.isActive ?: false
    val queueState get() = _queueState.asStateFlow()


    companion object {
        const val CHANNEL_ID = 100
        const val CHANNEL_NAME = "com.discut.manga.service.saver.download.model.DownloadScope"

        const val MAX_DOWNLOADS = 3
    }


    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_NAME).apply {
            setContentTitle("Download")
            setSmallIcon(android.R.drawable.stat_sys_download)
        }.build()
        return ForegroundInfo(
            CHANNEL_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
        )
    }

    override suspend fun doWork(): Result {
        var checkNetworkState = checkNetworkState(
            applicationContext.activeNetworkState(),
            downloadPreference.isWifiOnly()
        )
        var active = checkNetworkState && bootDownloadJob()
        if (active.not()) {
            return Result.failure()
        }

        try {
            setForeground(getForegroundInfo())
            delay(500)
        } catch (e: IllegalStateException) {
            Log.e("DownloadScope", e.message.toString())
            return Result.failure()
        }
        coroutineScope {
            combineTransform(
                applicationContext.networkStateFlow(),
                downloadPreference.getIsWifiOnlyAsFlow(),
                transform = { a, b -> emit(checkNetworkState(a, b)) },
            )
                .onEach { checkNetworkState = it }
                .launchIn(this)
        }
        // Keep the worker running when needed
        while (active) {
            active = !isStopped /*&& downloadManager.isRunning*/ && checkNetworkState
        }
        return Result.success()
    }

    private fun checkNetworkState(state: NetworkState, requireWifi: Boolean): Boolean {
        return if (state.isOnline) {
            val noWifi = requireWifi && !state.isWifi
            if (noWifi) {
                downloadManager.downloaderStop(
                    applicationContext.getString(R.string.download_notifier_text_only_wifi),
                )
            }
            !noWifi
        } else {
            downloadManager.downloaderStop(applicationContext.getString(R.string.download_notifier_no_network))
            false
        }
    }

    fun add(downloader: Downloader) {
        _queueState.value.find { it.download.id == downloader.download.id } ?: _queueState.update {
            it + downloader
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun bootDownloadJob(): Boolean {
        if (isRunning || _queueState.value.isEmpty()) {
            return false
        }
        downloadJob = scope.launchIO {
            val activeDownloadsFlow = queueState.transformLatest { queue ->
                while (true) {
                    delay(500)
                    val activeDownloads = queue.asSequence()
                        .filterAndSortNeedDownload()
                        .toList()//.take(MAX_DOWNLOADS) // Concurrently download from 3 different sources
                    if (activeDownloaderJobs.size < MAX_DOWNLOADS) {
                        emit(activeDownloads)
                    }

                    if (activeDownloads.isEmpty()) break
                    // Suspend until a download enters the ERROR state
                    val activeDownloadsErroredFlow =
                        combine(activeDownloads.map(Downloader::statusFlow)) { states ->
                            states.filterIsInstance<Downloader.DownloadState.Error>().isEmpty()
                                .not()
                        }.filter { it }
                    activeDownloadsErroredFlow.first()
                }
            }.distinctUntilChanged()

            // Use supervisorScope to cancel child jobs when the downloader job is cancelled
            supervisorScope {
                activeDownloadsFlow.collectLatest { activeDownloads ->
                    activeDownloaderJobs.forEach { (downloader, job) ->
                        if (downloader.status == Downloader.DownloadState.Downloaded) {
                            job.cancel()
                            activeDownloaderJobs.remove(downloader)
                        }
                    }
                    if (activeDownloaderJobs.size >= MAX_DOWNLOADS) return@collectLatest
                    activeDownloads.forEach { download ->
                        activeDownloaderJobs[download] = launchDownloaderJob(download)
                    }
                }
            }
        }
        return false
    }

    private fun CoroutineScope.launchDownloaderJob(downloader: Downloader): Job = launchIO {
        try {
            if (downloader.status == Downloader.DownloadState.Downloaded) {
                throw IllegalStateException("${downloader.getTag()}: status is ${downloader.status}, can't download again.")
            }
            var pages = if (downloader.pages == null || downloader.pages?.isEmpty() == true) {
                source.getPageList(downloader.chapter.toSChapter())
            } else {
                downloader.pages!!
            }.sortedBy { it.index }
            if (pages.isEmpty()) {
                throw IllegalStateException("${downloader.getTag()}: pages is empty.")
            }
            pages.forEachIndexed { index, page ->
                if (index < downloader.download.currentPage) return@forEachIndexed

                source.getImage(page)?.let {

                }
            }


        } catch (e: Throwable) {
            if (e is CancellationException) throw e
            logcat(LogPriority.ERROR, e)
            notifier.onError(e.message)
            stop()
        }
    }

    fun Downloader.getTag() = "${manga.title}->${chapter.name}(${source.name}id:${source.id})"

    fun startAll() {

    }

    fun stopAll() {

    }

    fun stop(downloader: Downloader) {

    }

    private fun Sequence<Downloader>.filterAndSortNeedDownload(): Sequence<Downloader> =
        filter {
            when (it.status) {
                Downloader.DownloadState.Waiting, Downloader.DownloadState.Downloading -> true
                else -> false
            }
        }.sortedWith { l, r ->
            if (l.status is Downloader.DownloadState.Downloading) {
                0
            } else {
                1
            }
        }
}
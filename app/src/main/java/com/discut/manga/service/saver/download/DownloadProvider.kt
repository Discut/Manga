package com.discut.manga.service.saver.download

import android.content.Context
import android.util.Log
import com.discut.manga.App
import com.discut.manga.data.SnowFlakeUtil
import com.discut.manga.data.manga.isLocal
import com.discut.manga.data.transitionToDownloaderState
import com.discut.manga.service.GlobalModuleEntrypoint
import com.discut.manga.service.chapter.page.PagesGetter
import com.discut.manga.service.saver.download.model.DownloadEvent
import com.discut.manga.service.saver.download.model.DownloadScope
import com.discut.manga.service.saver.download.model.DownloadWorker
import com.discut.manga.service.saver.download.model.Downloader
import com.discut.manga.service.source.SourceManager
import com.discut.manga.util.get
import com.discut.manga.util.launchIO
import com.discut.manga.util.withIOContext
import dagger.hilt.EntryPoints
import dagger.hilt.android.qualifiers.ApplicationContext
import discut.manga.data.MangaAppDatabase
import discut.manga.data.chapter.Chapter
import discut.manga.data.download.Download
import discut.manga.data.download.DownloadState
import discut.manga.data.download.DownloadState.Completed
import discut.manga.data.download.DownloadState.InQueue
import discut.manga.data.download.DownloadState.NotInQueue
import discut.manga.data.manga.Manga
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import manga.core.base.BaseManager
import manga.core.preference.DownloadPreference
import manga.core.preference.PreferenceManager
import manga.source.HttpSource
import manga.source.domain.Page
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sourceManager: SourceManager,
    private val pagesGetterFactory: PagesGetter.Factory
) : BaseManager {
    private val downloadDb by lazy { MangaAppDatabase.DB.downloadDao() }
    private val mangaDb by lazy { MangaAppDatabase.DB.mangaDao() }
    private val chapterDb by lazy { MangaAppDatabase.DB.chapterDao() }
    private val downloadPreference by lazy { PreferenceManager.get<DownloadPreference>() }

    private val _queue: MutableStateFlow<List<DownloadScope>> =
        MutableStateFlow(mutableListOf())

    val queue
        get() = _queue.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val eventFlow: MutableStateFlow<DownloadEvent?> =
        MutableStateFlow(null)

    private suspend fun buildDownloader(
        manga: Manga,
        chapter: Chapter,
        source: HttpSource
    ): Downloader =
        withIOContext {
            val pages = pagesGetterFactory.create(source).getPages(chapter)
            if (pages.isEmpty()) {
                throw IllegalArgumentException("Pages not found")
            }
            val mangaId = manga.id
            val chapterId = chapter.id
            val download = downloadDb.getByMangaIdAndChapterId(mangaId, chapterId) ?: let {
                val order = downloadDb.getAllByMangaId(mangaId).let { downloads ->
                    if (downloads.isEmpty()) {
                        0.0
                    } else {
                        downloads.maxByOrNull { it.order }!!.order + 1
                    }
                }
                Download(
                    id = SnowFlakeUtil.generateSnowFlake(),
                    mangaId = mangaId,
                    chapterId = chapterId,
                    order = order,
                    status = InQueue,
                    downloaded = listOf(),
                    queue = (0L..<pages.size).toList(),
                    addAt = System.currentTimeMillis(),
                    remarks = "",
                )
            }
            Downloader(
                source = source,
                download = download,
                manga = manga,
                chapter = chapter,
                pages = pages.onEachIndexed { index, page ->
                    if (index.toLong() in download.downloaded) {
                        page.status = Page.State.READY
                    }
                },
            ).apply {
                status = download.transitionToDownloaderState()
            }
        }

    fun sendEvent(builder: () -> DownloadEvent) {
        eventFlow.update {
            builder()
        }
    }

    private fun observer() {
        scope.launchIO {
            eventFlow
                .buffer()
                .filterNotNull()
                .collect { event ->
                    when (event) {
                        is DownloadEvent.DownloadScopeStop -> {
                            queue.value.find { it.scopeTag == event.scopeKey }?.let {
                                DownloadWorker.stopScope(context, it)
                            }
                        }

                    }
                }
        }
    }

    private suspend fun tryLaunchAllDownloadScope() {
        loadAndUpdate()
        Log.d("DownloadProvider", "try launch all download scope by worker")
        queue.value.filter { !DownloadWorker.isScopeRunning(context, it) }.onEach {
            Log.d("DownloadProvider", "try launch download scope by worker: ${it.scopeTag}")
            DownloadWorker.bootScope(context, it)
        }
    }

    private fun tryStopAllDownloadScope() {
        queue.value.filter { DownloadWorker.isScopeRunning(context, it) }.onEach {
            DownloadWorker.stopScope(context, it)
        }
    }

    fun launchDownloadScope(key: String): Boolean =
        queue.value.find { it.scopeTag == key }?.bootDownloadMainJob() ?: false


    suspend fun retryDownload(downloader: Downloader) {
        withIOContext {
            pauseDownload(downloader)
            startDownload(downloader)
        }
    }


    suspend fun deleteDownload(mangaId: Long, chapterId: Long) {
        withIOContext {
            checkDataAndRun(mangaId, chapterId) { manga, chapter, source ->
                cancelDownload(buildDownloader(manga, chapter, source))
            }
        }
    }


    /**
     * 取消下载
     */
    suspend fun cancelDownload(downloader: Downloader) {
        withIOContext {
            downloadDb.getByMangaIdAndChapterId(downloader.manga.id, downloader.chapter.id)
                ?.let {
                    queue.value.find { it.source.id == downloader.source.id }?.let {
                        it.cancel(downloader)
                        downloadDb.delete(downloader.download)
                        DownloadFileSystem(downloadPreference.getDownloadDirectory(App.instance))
                    }

                }
        }
    }

    fun cancelDownloads() {
        queue.value.forEach { it ->
            it.cancelAll()
            it.queueState.value.queue.filter { it.status == Downloader.DownloadState.Downloading || it.status == Downloader.DownloadState.Waiting }
                .onEach { downloader ->
                    downloadDb.getByMangaIdAndChapterId(downloader.manga.id, downloader.chapter.id)
                        ?.let {
                            queue.value.find { it.source.id == downloader.source.id }?.let {
                                downloadDb.delete(downloader.download)
                                DownloadFileSystem(downloadPreference.getDownloadDirectory(App.instance))
                            }

                        }
                }
        }
    }

    suspend fun pauseDownload(downloader: Downloader) {
        if (downloader.status != Downloader.DownloadState.Downloading &&
            downloader.status != Downloader.DownloadState.Waiting
        ) {
            return
        }
        queue.value.find { it.source.id == downloader.source.id }?.pause(downloader)
    }

    fun pauseDownloads() {
        tryStopAllDownloadScope()
        /*        queue.value.onEach {
                    tryStopAllDownloadScope()
                }*/
    }

    suspend fun startDownload(downloader: Downloader) {
        if (downloader.status != Downloader.DownloadState.InQueue) {
            return
        }
        queue.value.find { it.source.id == downloader.source.id }?.start(downloader)
        tryLaunchAllDownloadScope()
    }

    suspend fun startDownloads() {
        loadAndUpdate()
        queue.value.onEach {
            it.startAll()
        }
        tryLaunchAllDownloadScope()
    }

    suspend fun updateQueueOrder(source: HttpSource, orders: Map<Long, Double>) {
        queue.value.find { it.source.id == source.id }?.updateOrder(orders)
    }

    suspend fun addDownload(mangaId: Long, chapterId: Long) {
        withIOContext {
            checkDataAndRun(mangaId, chapterId) { manga, chapter, source ->
                if (manga.isLocal()) {
                    throw IllegalArgumentException("Manga $mangaId is local")
                }
                getDownloadScope(source).apply {
                    buildDownloader(manga, chapter, source).apply {
                        add(this)
                        updateOrInsertDownload(download)
                    }
                }
                tryLaunchAllDownloadScope()
            }
        }
    }

    private fun getDownloadScope(source: HttpSource): DownloadScope =
        try {
            queue.value.first { it.source.id == source.id }
        } catch (_: Exception) {
            DownloadScope().apply {
                _queue.update {
                    _queue.value + this
                }
            }
        }.apply {
            this.source = source
        }


    private fun clearDb() {
        downloadDb.getAll().filter { it.status != Completed }.forEach {
            downloadDb.delete(it)
        }
    }

    /**
     * Rinse the db.
     *
     * Remove all downloads when their comics and chapters do not exist in the database
     */
    private fun rinseDb() {
        downloadDb.getAll().filter { it.status != Completed }.forEach {
            if (mangaDb.getById(it.mangaId) == null ||
                chapterDb.getById(it.chapterId) == null
            ) {
                downloadDb.delete(it)
            }
        }
    }

    suspend fun getAllDownloads() =
        downloadDb.getAllAsFlow().map { downloadList ->
            downloadList
                .filter { it.status != Completed }
                .map {
                    var downloader: Downloader? = null
                    checkDataAndRun(it.mangaId, it.chapterId) { manga, chapter, source ->
                        downloader = buildDownloader(manga, chapter, source)
                    }
                    downloader!!
                }.groupBy { it.source }
        }

    fun updateOrInsertDownload(download: Download) {
        if (downloadDb.getById(download.id) == null)
            downloadDb.insert(download)
        else
            downloadDb.update(download)
    }

    fun updateOrder(orders: Map<Long, Double>) {
        orders.forEach(downloadDb::updateOrder)
    }

    fun getDownloadState(mangaId: Long, chapterId: Long): DownloadState {
        queue.value.forEach { downloadScope ->
            downloadScope.queueState.value.queue.find { it.manga.id == mangaId && it.chapter.id == chapterId }
                ?.let { return it.download.status }
        }
        return NotInQueue
    }

    fun isDownloaded(id: Long, mangaId: Long): Boolean {
        val download = downloadDb.getByMangaIdAndChapterId(mangaId, id) ?: return false
        return download.status == Completed
    }

    fun subscribe(mangaId: Long, chapterId: Long): Flow<Download?> {
        return downloadDb.getByMangaIdAndChapterIdAsFlow(mangaId, chapterId)
    }

    private suspend fun loadAndUpdate() = withIOContext {
        downloadDb.getAll().filter { it.status != Completed }
            .sortedBy { it.order }
            .forEach {
                checkDataAndRun(it.mangaId, it.chapterId) { manga, chapter, source ->
                    val scope = getDownloadScope(source)

                    val downloader = buildDownloader(
                        manga, chapter, source
                    )

                    Downloader(
                        source = source,
                        download = it,
                        manga = manga,
                        chapter = chapter,
                        pages = pagesGetterFactory.create(source).getPages(chapter)

                    )
                    scope.add(downloader)
                }
            }
    }

    private suspend fun checkDataAndRun(
        mangaId: Long, chapterId: Long, action: suspend (
            Manga, Chapter, HttpSource
        ) -> Unit
    ) {
        val manga = mangaDb.getById(mangaId)
            ?: throw IllegalArgumentException("Manga $mangaId not found")
        val chapter = chapterDb.getById(chapterId)
            ?: throw IllegalArgumentException("Chapter $chapterId not found")
        val source = sourceManager.get(manga.source) as? HttpSource
            ?: throw IllegalArgumentException("Source ${manga.source} not found")
        action(manga, chapter, source)
    }


    companion object {
        const val TAG = "DownloadProvider"
    }

    override fun initManager() {
        Log.d(TAG, "initManager")
        observer()
        scope.launchIO {
            rinseDb()
            tryLaunchAllDownloadScope()
        }
    }

}

val DownloadProvider.Companion.instance: DownloadProvider
    get() = EntryPoints.get(App.instance, GlobalModuleEntrypoint::class.java)
        .getDownloadProviderInstance()
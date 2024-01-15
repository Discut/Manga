package com.discut.manga.service.saver.download

import android.content.Context
import com.discut.manga.App
import com.discut.manga.data.SnowFlakeUtil
import com.discut.manga.data.manga.isLocal
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
import discut.manga.data.download.Download
import discut.manga.data.download.DownloadState
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
import manga.source.HttpSource
import manga.core.preference.DownloadPreference
import manga.core.preference.PreferenceManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sourceManager: SourceManager,
    private val pagesGetterFactory: PagesGetter.Factory
) {
    private val downloadDb = MangaAppDatabase.DB.downloadDao()
    private val mangaDb = MangaAppDatabase.DB.mangaDao()
    private val chapterDb = MangaAppDatabase.DB.chapterDao()
    private val downloadPreference = PreferenceManager.get<DownloadPreference>()

    private val _queue: MutableStateFlow<MutableList<DownloadScope>> =
        MutableStateFlow(mutableListOf())

    val queue
        get() = _queue.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val eventFlow: MutableStateFlow<DownloadEvent?> =
        MutableStateFlow(null)

    init {
        scope.launchIO {
            rinseDb()
            downloadDb.getAll()/*.filter { it.status != DownloadState.Completed }*/.forEach { it ->
                val manga = mangaDb.getById(it.mangaId)
                val chapter = chapterDb.getById(it.chapterId)
                    ?: throw IllegalArgumentException("Chapter ${it.chapterId} not found")
                val source = sourceManager.get(manga!!.source) as? HttpSource
                    ?: throw IllegalArgumentException("Source ${manga.source} not found")
                val scope = getDownloadScope(source)

                val downloader = Downloader(
                    source = source,
                    download = it,
                    manga = manga,
                    chapter = chapter,
                    pages = pagesGetterFactory.create(source).getPages(chapter)

                )
                scope.add(downloader)
            }
        }
        tryLaunchAllDownloadScope()
        observer()
    }

    private suspend fun buildDownloader(mangaId: Long, chapterId: Long): Downloader =
        withIOContext {
            val manga = mangaDb.getById(mangaId)
            val chapter = chapterDb.getById(chapterId)
                ?: throw IllegalArgumentException("Chapter $chapterId not found")
            val source = sourceManager.get(manga!!.source) as? HttpSource
                ?: throw IllegalArgumentException("Source ${manga.source} not found")
            val pages = pagesGetterFactory.create(source).getPages(chapter)
            if (pages.isEmpty()) {
                throw IllegalArgumentException("Pages not found")
            }
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
                    status = DownloadState.InQueue,
                    downloaded = listOf(),
                    queue = (0L..<pages.size).toList(),
                    addAt = System.currentTimeMillis()
                )
            }
            Downloader(
                source = source,
                download = download,
                manga = manga,
                chapter = chapter,
                pages = pages
            )
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

                        else -> {}
                    }
                }
        }
    }

    private fun tryLaunchAllDownloadScope() {
        queue.value.filter { !DownloadWorker.isScopeRunning(context, it) }.onEach {
            DownloadWorker.bootScope(context, it)
        }
    }

    fun launchDownloadScope(key: String): Boolean =
        queue.value.find { it.scopeTag == key }?.bootDownloadMainJob() ?: false


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

    fun pauseDownload(downloader: Downloader) {
        if (downloader.status != Downloader.DownloadState.Downloading) {
            return
        }
        queue.value.find { it.source.id == downloader.source.id }?.pause(downloader)
    }

    fun pauseDownloads() {
        queue.value.onEach {
            it.pauseAll()
        }
    }

    fun startDownload(downloader: Downloader) {

    }

    suspend fun updateQueueOrder(/*downloader: Downloader*/) {
        queue.value.forEach{
            it.updateOrder()
        }
        //queue.value.find { it.source.id == downloader.source.id }?.updateOrder()
    }

    suspend fun addDownload(mangaId: Long, chapterId: Long) {
        withIOContext {
            val manga =
                mangaDb.getById(mangaId)
                    ?: throw IllegalArgumentException("Manga $mangaId not found")
            val chapter = chapterDb.getById(chapterId)
                ?: throw IllegalArgumentException("Chapter $chapterId not found")
            val source = sourceManager.get(manga.source) as? HttpSource
                ?: throw IllegalArgumentException("Source ${manga.source} not remote")
            if (manga.isLocal()) {
                throw IllegalArgumentException("Manga $mangaId is local")
            }
            getDownloadScope(source).apply {
                add(buildDownloader(mangaId, chapterId))
            }
            tryLaunchAllDownloadScope()
        }
    }

    private fun getDownloadScope(source: HttpSource): DownloadScope =
        try {
            queue.value.first { it.source.id == source.id }
        } catch (_: Exception) {
            DownloadScope().apply {
                _queue.update {
                    it.add(this@apply)
                    it
                }
            }
        }.apply {
            this.source = source
        }


    private fun clearDb() {
        downloadDb.getAll().filter { it.status != DownloadState.Completed }.forEach {
            downloadDb.delete(it)
        }
    }

    /**
     * Rinse the db.
     *
     * Remove all downloads when their comics and chapters do not exist in the database
     */
    private fun rinseDb() {
        downloadDb.getAll().filter { it.status != DownloadState.Completed }.forEach {
            if (mangaDb.getById(it.mangaId) == null ||
                chapterDb.getById(it.chapterId) == null
            ) {
                downloadDb.delete(it)
            }
        }
    }

    fun getAllDownloads() = _queue.map { it ->
        it.map { it.queueState }
    }

    fun updateOrInsertDownload(download: Download) {
        if (downloadDb.getById(download.id) == null)
            downloadDb.insert(download)
        else
            downloadDb.update(download)
    }

    fun getDownloadState(mangaId: Long, chapterId: Long): DownloadState {
        queue.value.forEach { downloadScope ->
            downloadScope.queueState.value.find { it.manga.id == mangaId && it.chapter.id == chapterId }
                ?.let { return it.download.status }
        }
        return DownloadState.NotInQueue
    }

    fun isDownloaded(id: Long, mangaId: Long): Boolean {
        val download = downloadDb.getByMangaIdAndChapterId(mangaId, id) ?: return false
        return download.status == DownloadState.Completed
    }

    fun subscribe(mangaId: Long, chapterId: Long): Flow<Download?> {
        return downloadDb.getByMangaIdAndChapterIdAsFlow(mangaId, chapterId)
    }


    companion object {
        const val TAG = "DownloadProvider"
    }

}

val DownloadProvider.Companion.instance: DownloadProvider
    get() = EntryPoints.get(App.instance, GlobalModuleEntrypoint::class.java)
        .getDownloadProviderInstance()
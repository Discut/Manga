package com.discut.manga.service.saver.download

import android.content.Context
import com.discut.manga.data.generateHashKey
import com.discut.manga.data.toSChapter
import com.discut.manga.service.cache.PagesCache
import com.discut.manga.service.saver.SaverService
import com.discut.manga.service.saver.download.model.DownloadScope
import com.discut.manga.service.saver.download.model.Downloader
import com.discut.manga.service.source.SourceManager
import com.discut.manga.util.launchIO
import dagger.hilt.android.qualifiers.ApplicationContext
import discut.manga.data.MangaAppDatabase
import discut.manga.data.download.DownloadState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import managa.source.HttpSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sourceManager: SourceManager,
    private val pagesCache: PagesCache
) {
    private val downloadDb = MangaAppDatabase.DB.downloadDao()
    private val mangaDb = MangaAppDatabase.DB.mangaDao()
    private val chapterDb = MangaAppDatabase.DB.chapterDao()

    private val queue: MutableList<DownloadScope> = mutableListOf()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        rinseDb()
        scope.launchIO {
            downloadDb.getAll().filter { it.status != DownloadState.Completed }.forEach { it ->
                val manga = mangaDb.getById(it.mangaId)
                val chapter = chapterDb.getById(it.chapterId)
                    ?: throw IllegalArgumentException("Chapter ${it.chapterId} not found")
                val source = sourceManager.get(manga!!.source) as? HttpSource
                    ?: throw IllegalArgumentException("Source ${manga.source} not found")
                val scope = try {
                    queue.first { it.source.id == manga.source }
                } catch (_: Exception) {
                    DownloadScope().apply {
                        queue.add(this)
                    }
                }.apply {
                    this.source = source
                }

                val downloader = Downloader(
                    source = source,
                    download = it,
                    manga = manga,
                    chapter = chapter,
                    pages = if (pagesCache.isExist(chapter.generateHashKey())) {
                        Json.decodeFromString(chapter.generateHashKey())
                    } else {
                        source.getPageList(chapter.toSChapter()).apply {
                            pagesCache.put(chapter.generateHashKey(), Json.encodeToString(this))
                        }
                    }

                )
                scope.add(downloader)
            }
        }
    }

    /**
     * Tells the downloader to begin downloads.
     */
    fun startDownloads() {
        SaverService.start(context)
    }

    /**
     * Tells the downloader to pause downloads.
     */
    fun pauseDownloads() {
        queue.onEach {
            it.stopAll()
        }
    }

    /**
     * Empties the download queue.
     */
    fun clearQueue() {
        queue.onEach {
            it.stopAll()
        }
        clearDb()
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
}
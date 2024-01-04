package com.discut.manga.service.manga

import androidx.paging.PagingSource
import com.discut.manga.data.manga.UpdateManga
import com.discut.manga.service.manga.paging.SourcePopularPagingSource
import com.discut.manga.source.ISourceManager
import discut.manga.data.MangaAppDatabase
import discut.manga.data.manga.Manga
import kotlinx.coroutines.flow.Flow
import managa.source.domain.FilterList
import managa.source.domain.SManga
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaProviderImpl @Inject constructor(
    private val sourceManager: ISourceManager,
    private val fetchInterval: FetchInterval
) : IMangaProvider {

    private val mangaDb = MangaAppDatabase.DB.mangaDao()
    override fun search(
        sourceId: Long,
        query: String,
        filterList: FilterList
    ): PagingSource<Long, SManga> {
        TODO("Not yet implemented")
    }

    override fun getPopular(sourceId: Long): PagingSource<Long, SManga> {
        val source = sourceManager.get(sourceId) ?: throw Exception("Source not found")
        return SourcePopularPagingSource(source)
    }

    override fun getLatest(sourceId: Long): PagingSource<Long, SManga> {
        TODO("Not yet implemented")
    }

    override fun subscribe(mangaId: Long): Flow<Manga> {
        return mangaDb.getByIdAsFlow(mangaId)
    }

    override fun update(mangaId: Long, builder: UpdateManga.() -> Unit): Int {
        val updateManga = UpdateManga(mangaId)
        builder(updateManga)
        return update(updateManga)
    }


    override fun update(updateManga: UpdateManga): Int {
        synchronized(this) {
            return mangaDb.getById(updateManga.id)?.let {
                mangaDb.update(
                    it.copy(
                        source = updateManga.source ?: it.source,
                        favorite = updateManga.favorite ?: it.favorite,
                        lastUpdate = updateManga.lastUpdate ?: it.lastUpdate,
                        nextUpdate = updateManga.nextUpdate ?: it.nextUpdate,
                        fetchInterval = updateManga.fetchInterval ?: it.fetchInterval,
                        dateAdded = updateManga.dateAdded ?: it.dateAdded,
                        viewerFlags = updateManga.viewerFlags ?: it.viewerFlags,
                        chapterFlags = updateManga.chapterFlags ?: it.chapterFlags,
                        coverLastModified = updateManga.coverLastModified ?: it.coverLastModified,
                        url = updateManga.url ?: it.url,
                        title = updateManga.title ?: it.title,
                        artist = updateManga.artist ?: it.artist,
                        author = updateManga.author ?: it.author,
                        description = updateManga.description ?: it.description,
                        genre = updateManga.genre ?: it.genre,
                        status = updateManga.status ?: it.status,
                        thumbnailUrl = updateManga.thumbnailUrl ?: it.thumbnailUrl,
                        category = updateManga.category ?: it.category,
                        initialized = updateManga.initialized ?: it.initialized
                    )
                )
            } ?: 0
        }
    }


    override suspend fun updateFetchInterval(
        manga: Manga,
        dateTime: ZonedDateTime?,
        window: FetchWindow?,
    ): Boolean {
        val innerDataTime = dateTime ?: ZonedDateTime.now()
        val innerWindow = window ?: fetchInterval.getWindow(innerDataTime)
        return fetchInterval.toUpdatedMangaOrNull(
            manga,
            innerDataTime,
            innerWindow
        )?.let {
            update(it) > 0
        } ?: false
    }

}
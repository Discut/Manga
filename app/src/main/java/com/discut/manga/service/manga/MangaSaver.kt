package com.discut.manga.service.manga

import com.discut.manga.data.manga.isLocal
import com.discut.manga.data.manga.toSManga
import com.discut.manga.data.manga.toUpdateManga
import com.discut.manga.service.source.ISourceManager
import discut.manga.data.MangaAppDatabase
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaSaver @Inject constructor(
    private val sourceManager: ISourceManager,
    private val mangaProvider:IMangaProvider
) {
    private val db = MangaAppDatabase.DB.mangaDao()

    suspend fun update(
        mangaId: Long,
        sourceId: Long,
        manualFetch: Boolean = false,
    ) {
        val dbManga = db.getById(mangaId) ?: throw Exception("Manga not found")
        val source = sourceManager.get(sourceId) ?: throw Exception("Source not found")
        val sManga = source.getMangaDetails(dbManga.toSManga())

        // if the manga isn't a favorite, set its title from source and update in db
        val title = if (sManga.title.isEmpty() || dbManga.favorite) sManga.title else dbManga.title
        val coverLastModified =
            when {
                // Never refresh covers if the url is empty to avoid "losing" existing covers
                sManga.thumbnail_url.isNullOrEmpty() -> null
                !manualFetch && dbManga.thumbnailUrl == sManga.thumbnail_url -> null
                dbManga.isLocal() -> Date().time
                /*                dbManga.hasCustomCover(coverCache) -> {
                                    coverCache.deleteFromCache(dbManga, false)
                                    null
                                }*/
                else -> {
                    //coverCache.deleteFromCache(dbManga, false)
                    Date().time
                }
            }

        val thumbnailUrl = sManga.thumbnail_url?.takeIf { it.isNotEmpty() }

        mangaProvider.update(dbManga.id){
            this.title = title
            this.coverLastModified = coverLastModified ?: dbManga.coverLastModified
            author = sManga.author
            artist = sManga.artist
            description = sManga.description
            genre = sManga.getGenres()
            this.thumbnailUrl = thumbnailUrl
            status = sManga.status.toLong()
            // updateStrategy = remoteManga.update_strategy,
            initialized = true
        }

/*        mangaProvider.update(
            dbManga.toUpdateManga().copy(
                title = title,
                coverLastModified = coverLastModified ?: dbManga.coverLastModified,
                author = sManga.author,
                artist = sManga.artist,
                description = sManga.description,
                genre = sManga.getGenres(),
                thumbnailUrl = thumbnailUrl,
                status = sManga.status.toLong(),
                // updateStrategy = remoteManga.update_strategy,
                initialized = true,
            )
        )*/
    }

}
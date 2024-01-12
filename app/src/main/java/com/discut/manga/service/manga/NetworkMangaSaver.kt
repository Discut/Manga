package com.discut.manga.service.manga

import com.discut.manga.data.manga.toManga
import com.discut.manga.util.withIOContext
import discut.manga.data.MangaAppDatabase
import discut.manga.data.manga.Manga
import manga.source.domain.SManga
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMangaSaver @Inject constructor(
) {
    suspend fun trySave(manga: SManga, mangaId: Long): Manga =
        trySave(manga.toManga(mangaId))


    private suspend fun trySave(manga: Manga): Manga =
        withIOContext {
            val dbManga =
                MangaAppDatabase.DB.mangaDao().getByUrlAndSource(manga.url, manga.source)
            return@withIOContext when {
                dbManga == null -> {
                    MangaAppDatabase.DB.mangaDao().insert(manga)
                    manga
                }

                !dbManga.favorite -> {
                    dbManga.copy(title = manga.title)
                }

                else -> {
                    manga.copy(
                        id = dbManga.id,
                    )
                }
            }
        }


}
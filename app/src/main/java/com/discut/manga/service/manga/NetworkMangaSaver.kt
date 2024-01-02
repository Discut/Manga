package com.discut.manga.service.manga

import com.discut.manga.data.extensions.toManga
import discut.manga.data.MangaAppDatabase
import discut.manga.data.manga.Manga
import managa.source.domain.SManga
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMangaSaver @Inject constructor(
) {
    fun trySave(manga: SManga, mangaId: Long): Manga =
        trySave(manga.toManga(mangaId))


    private fun trySave(manga: Manga): Manga {
        val dbManga =
            MangaAppDatabase.DB.mangaDao().getByUrlAndSource(manga.url, manga.source)
        return when {
            dbManga == null -> {
                MangaAppDatabase.DB.mangaDao().insert(manga)
                manga
            }

            !dbManga.favorite -> {
                dbManga.copy(title = manga.title)
            }

            else -> {
                manga
            }
        }
    }
}
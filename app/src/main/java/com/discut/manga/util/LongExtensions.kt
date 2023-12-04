package com.discut.manga.util

import discut.manga.data.MangaAppDatabase
import discut.manga.data.manga.Manga
import discut.manga.data.manga.MangaDao

fun Long?.toMangaFromDB(mangaDao: MangaDao = MangaAppDatabase.DB.mangaDao()): Manga? {
    if (this == null) return null
    return mangaDao.getById(this)
}
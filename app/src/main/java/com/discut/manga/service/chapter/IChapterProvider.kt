package com.discut.manga.service.chapter

import discut.manga.data.chapter.Chapter
import kotlinx.coroutines.flow.Flow

interface IChapterProvider {
    fun subscribe(mangaId: Long): Flow<List<Chapter>>

    fun insertAllTo(chapters: Chapter, mangaId: Long)
}
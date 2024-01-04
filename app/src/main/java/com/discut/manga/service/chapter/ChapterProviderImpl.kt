package com.discut.manga.service.chapter

import discut.manga.data.MangaAppDatabase
import discut.manga.data.chapter.Chapter
import discut.manga.data.chapter.ChapterDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChapterProviderImpl @Inject constructor(
) : IChapterProvider {

    private val db: ChapterDao = MangaAppDatabase.DB.chapterDao()
    override fun subscribe(mangaId: Long): Flow<List<Chapter>> {
        return db.getAllInMangaAsFlow(mangaId)
    }

    override fun insertAllTo(chapters: Chapter, mangaId: Long) {

    }
}
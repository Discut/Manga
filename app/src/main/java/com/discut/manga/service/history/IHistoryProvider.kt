package com.discut.manga.service.history

import com.discut.manga.domain.history.MangaChapterHistory
import kotlinx.coroutines.flow.Flow

interface IHistoryProvider {
    fun subscribeAll(): Flow<List<MangaChapterHistory>>

    fun subscribe(mangaId: Long): Flow<MangaChapterHistory?>

    fun insert(history: MangaChapterHistory)

    fun removeAll(histories: List<MangaChapterHistory>): Int

    fun getLatest(mangaId: Long): MangaChapterHistory?

}
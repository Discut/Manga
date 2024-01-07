package com.discut.manga.service.history

import com.discut.manga.domain.history.MangaChapterHistory
import kotlinx.coroutines.flow.Flow

interface IHistoryProvider {
    fun subscribeAll(): Flow<List<MangaChapterHistory>>

    fun insert(history: MangaChapterHistory)

    fun removeAll(histories: List<MangaChapterHistory>): Int

}
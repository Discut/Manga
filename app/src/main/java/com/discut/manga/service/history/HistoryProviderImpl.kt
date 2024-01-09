package com.discut.manga.service.history

import com.discut.manga.data.SnowFlakeUtil
import com.discut.manga.domain.history.MangaChapterHistory
import com.discut.manga.util.get
import discut.manga.data.MangaAppDatabase
import discut.manga.data.history.History
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import manga.core.preference.PreferenceManager
import manga.core.preference.SettingsPreference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryProviderImpl @Inject constructor() : IHistoryProvider {
    private val db = MangaAppDatabase.DB.historyDao()
    private val mangaDb = MangaAppDatabase.DB.mangaDao()
    private val chapterDb = MangaAppDatabase.DB.chapterDao()
    private val settingsPreference = PreferenceManager.get<SettingsPreference>()
    override fun subscribeAll(): Flow<List<MangaChapterHistory>> =
        db.getAllAsFlow().map {
            it.map { history ->
                val manga = mangaDb.getById(history.mangaId)
                MangaChapterHistory(
                    historyId = history.id,
                    mangaId = history.mangaId,
                    chapterId = history.chapterId,
                    mangaTitle = manga?.title ?: "",
                    chapterName = chapterDb.getById(history.chapterId)?.name ?: "",
                    thumbnailUrl = manga?.thumbnailUrl,
                    readAt = history.readAt
                )
            }.filter { mch -> mch.mangaTitle.isNotBlank() }
        }


    override fun insert(history: MangaChapterHistory) {
        if (settingsPreference.getNoTranceMode()) {
            return
        }
        when (val dbHistory = db.getByMangaId(history.mangaId)) {
            null -> {
                db.insert(
                    History(
                        id = SnowFlakeUtil.generateSnowFlake(),
                        mangaId = history.mangaId,
                        chapterId = history.chapterId,
                        readAt = history.readAt
                    )
                )
            }

            else -> {
                db.update(
                    dbHistory.copy(
                        chapterId = history.chapterId,
                        readAt = history.readAt
                    )
                )
            }
        }
    }

    override fun removeAll(histories: List<MangaChapterHistory>): Int {
        histories.forEach {
            db.delete(
                History(
                    id = it.historyId,
                    mangaId = -1,
                    chapterId = -1,
                    readAt = 0
                )
            )
        }
        return histories.size
    }

    override fun getLatest(mangaId: Long): MangaChapterHistory? {
        db.getByMangaId(mangaId)?.let {
            val manga = mangaDb.getById(it.mangaId)
            return MangaChapterHistory(
                historyId = it.id,
                mangaId = it.mangaId,
                chapterId = it.chapterId,
                mangaTitle = manga?.title ?: "",
                chapterName = chapterDb.getById(it.chapterId)?.name ?: "",
                thumbnailUrl = manga?.thumbnailUrl,
                readAt = it.readAt
            )
        }
        return null
    }

    override fun subscribe(mangaId: Long): Flow<MangaChapterHistory?> {
        return db.getByIdAsFlow(mangaId).map {
            if (it == null) {
                return@map null
            }
            val manga = mangaDb.getById(it.mangaId)
            MangaChapterHistory(
                historyId = it.id,
                mangaId = it.mangaId,
                chapterId = it.chapterId,
                mangaTitle = manga?.title ?: "",
                chapterName = chapterDb.getById(it.chapterId)?.name ?: "",
                thumbnailUrl = manga?.thumbnailUrl,
                readAt = it.readAt
            )
        }
    }

}
package com.discut.manga.ui.reader

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.ui.reader.domain.ReaderActivityEffect
import com.discut.manga.ui.reader.domain.ReaderActivityEvent
import com.discut.manga.ui.reader.domain.ReaderActivityState
import com.discut.manga.util.withIOContext
import dagger.hilt.android.lifecycle.HiltViewModel
import discut.manga.data.MangaAppDatabase
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ReaderActivityViewModel @Inject constructor() :
    BaseViewModel<ReaderActivityState, ReaderActivityEvent, ReaderActivityEffect>() {

    private val dbManager = MangaAppDatabase.DB
    override fun initialState(): ReaderActivityState {
        TODO("Not yet implemented")
    }

    override suspend fun handleEvent(
        event: ReaderActivityEvent,
        state: ReaderActivityState
    ): ReaderActivityState {
        return when (event) {
            is ReaderActivityEvent.Initialize -> {
                withContext(NonCancellable) {
                    init(event.mangaId, event.chapterId)
                }
                state
            }
        }
    }

    suspend fun init(mangaId: Long, chapterId: Long): Result<Boolean> {
        withIOContext {
            val byId = dbManager.mangaDao().getById(mangaId)
            TODO("需要去实现初始化漫画章节时，不同状态的 sendEffect，例如初始化失败")
            // 参考eu.kanade.tachiyomi.ui.reader.ReaderActivity#onCreate 中的 viewModel.init(manga, chapter)
            // 与 eu.kanade.tachiyomi.ui.reader.ReaderViewModel#init
        }
    }
}
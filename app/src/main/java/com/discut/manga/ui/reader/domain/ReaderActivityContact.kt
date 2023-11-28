package com.discut.manga.ui.reader.domain


import androidx.compose.runtime.Immutable
import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import com.discut.manga.ui.reader.viewer.domain.ReaderChapter
import discut.manga.data.manga.Manga

@Immutable
data class ReaderActivityState(
    val manga: Manga? = null,
    val readerChapters: List<ReaderChapter> = emptyList(),
    val currentChapters: CurrentChapters? = null,
    val currentPage: Int = -1
) : UiState

sealed interface ReaderActivityEvent : UiEvent {

    data class Initialize(val mangaId: Long, val chapterId: Long) : ReaderActivityEvent
}

sealed interface ReaderActivityEffect : UiEffect {
    data class InitMangaError(val error: Throwable) : ReaderActivityEffect
    data class InitChapterError(val error: Throwable) : ReaderActivityEffect
}
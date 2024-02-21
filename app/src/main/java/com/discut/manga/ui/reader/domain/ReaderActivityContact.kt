package com.discut.manga.ui.reader.domain


import androidx.compose.runtime.Immutable
import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import com.discut.manga.preference.ReaderMode
import discut.manga.data.manga.Manga

@Immutable
data class ReaderActivityState(
    val manga: Manga? = null,
    val readerChapters: List<ReaderChapter> = emptyList(),
    val currentChapters: CurrentChapters? = null,
    val currentPage: Int = -1,
    val isMenuShow: Boolean = false,

    val readerMode: ReaderMode
) : UiState {
    val readerPages: Int by lazy {
        when (val chapterState = currentChapters?.currReaderChapter?.state) {
            is ReaderChapter.State.Loaded -> chapterState.pages.size
            else -> 0
        }
    }
}

sealed interface ReaderActivityEvent : UiEvent {

    data class Initialize(val mangaId: Long, val chapterId: Long) : ReaderActivityEvent

    data class ReaderNavigationMenuVisibleChange(val visible: Boolean) : ReaderActivityEvent

    data class PageSelected(val index: Int) : ReaderActivityEvent

    data class ReaderModeChange(val mode: ReaderMode) : ReaderActivityEvent

    data class SwitchToChapter(val chapter: ReaderChapter) : ReaderActivityEvent

}

sealed interface ReaderActivityEffect : UiEffect {
    data class InitMangaError(val error: Throwable) : ReaderActivityEffect
    data class InitChapterError(val error: Throwable) : ReaderActivityEffect
}
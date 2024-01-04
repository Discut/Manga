package com.discut.manga.ui.manga.details

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import discut.manga.data.chapter.Chapter
import discut.manga.data.manga.Manga

data class MangaDetailsState(
    internal val loadState: LoadState = LoadState.Waiting,
    internal val isLoading: Boolean = false,
    internal val manga: Manga? = null,
    internal val chapters: List<Chapter> = emptyList()
) : UiState {
    sealed class LoadState {
        data object Waiting : LoadState()
        data class Loaded(val details: MangaDetails) : LoadState()
        data class Error(val error: Throwable) : LoadState()
        data object Loading : LoadState()
    }
}

sealed interface MangaDetailsEvent : UiEvent {

    data class Init(val mangaId: Long) : MangaDetailsEvent

    data object BootSync : MangaDetailsEvent

    data object Synced : MangaDetailsEvent

    data class ChaptersUpdated(val chapters: List<Chapter>) : MangaDetailsEvent

    data class ReadChapter(val chapter: Chapter) : MangaDetailsEvent

    data class UnreadChapter(val chapter: Chapter) : MangaDetailsEvent

    data class FavoriteManga(val manga: Manga) : MangaDetailsEvent

}

sealed interface MangaDetailsEffect : UiEffect {
    data class SecurityModeChange(val enable: Boolean) : MangaDetailsEffect

}


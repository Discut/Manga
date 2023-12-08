package com.discut.manga.ui.bookshelf

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import discut.manga.data.category.Category
import discut.manga.data.manga.Manga

typealias ShelfManga = Map<Category, List<Manga>>

data class BookshelfState(
    internal val loadState: LoadState = LoadState.Waiting,
    internal val categories: List<Category> = emptyList(),
) : UiState {
    sealed class LoadState {
        data object Waiting : LoadState()
        data class Loaded(val shelfManga: ShelfManga) : LoadState()
        data class Error(val error: Throwable) : LoadState()
        data object Loading : LoadState()
    }
}

sealed interface BookshelfEvent : UiEvent {

    data object Init : BookshelfEvent

}

sealed interface BookshelfEffect : UiEffect {
    data class SecurityModeChange(val enable: Boolean) : BookshelfEffect

}


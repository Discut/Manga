package com.discut.manga.ui.manga.details

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import com.discut.manga.domain.history.MangaChapterHistory
import discut.manga.data.category.Category
import discut.manga.data.chapter.Chapter
import discut.manga.data.manga.Manga
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MangaDetailsState(
    internal val loadState: LoadState = LoadState.Waiting,
    internal val isLoading: Boolean = false,
    internal val manga: Manga? = null,
    internal val categories: List<Category> = emptyList(),
    internal val chapters: List<Chapter> = emptyList(),
    internal val currentHistory: StateFlow<MangaChapterHistory?> = MutableStateFlow(null)
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

    data class BootSync(val mangaId: Long) : MangaDetailsEvent

    data object Synced : MangaDetailsEvent

    data class ChaptersUpdated(val chapters: List<Chapter>) : MangaDetailsEvent

    data class MangaUpdated(val manga: Manga) : MangaDetailsEvent

    data class ReadChapter(val chapter: Chapter) : MangaDetailsEvent

    data object StartToRead : MangaDetailsEvent

    data class UnreadChapter(val chapter: Chapter) : MangaDetailsEvent

    data class FavoriteManga(val manga: Manga) : MangaDetailsEvent

    data class AddNewCategory(val category: String) : MangaDetailsEvent

}

sealed interface MangaDetailsEffect : UiEffect {
    data class JumpToRead(val mangaId: Long, val chapterId: Long) : MangaDetailsEffect

}


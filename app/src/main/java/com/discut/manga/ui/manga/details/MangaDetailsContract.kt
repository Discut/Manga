package com.discut.manga.ui.manga.details

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import com.discut.manga.domain.history.MangaChapterHistory
import com.discut.manga.ui.manga.ChapterScope
import discut.manga.data.category.Category
import discut.manga.data.chapter.Chapter
import discut.manga.data.manga.Manga
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MangaDetailsState(
    internal val isLoading: Boolean = false,
    internal val manga: StateFlow<Manga?> = MutableStateFlow(null),
    internal val categories: List<Category> = emptyList(),
    internal val chapters: StateFlow<List<ChapterScope>> = MutableStateFlow(emptyList()),
    internal val currentHistory: StateFlow<MangaChapterHistory?> = MutableStateFlow(null)
) : UiState

sealed interface MangaDetailsEvent : UiEvent {

    data class Init(val mangaId: Long) : MangaDetailsEvent

    data class BootSync(val mangaId: Long) : MangaDetailsEvent

    data object Synced : MangaDetailsEvent

    data class ReadChapter(val chapter: Chapter) : MangaDetailsEvent

    data object StartToRead : MangaDetailsEvent

    data class UnreadChapter(val chapter: Chapter) : MangaDetailsEvent

    data class FavoriteManga(val manga: Manga) : MangaDetailsEvent

    data class AddNewCategory(val category: String) : MangaDetailsEvent

    data class DownloadChapter(val manga: Manga, val chapter: Chapter) : MangaDetailsEvent

    data class DeleteChapter(val manga: Manga, val chapter: Chapter) : MangaDetailsEvent

}

sealed interface MangaDetailsEffect : UiEffect {
    data class JumpToRead(val mangaId: Long, val chapterId: Long) : MangaDetailsEffect

    data class NetworkError(val error: Throwable, val msg: String) : MangaDetailsEffect

}


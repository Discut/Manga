package com.discut.manga.ui.bookshelf

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.data.category.DefaultCategories
import com.discut.manga.service.manga.IMangaProvider
import com.discut.manga.service.source.ISourceManager
import com.discut.manga.util.withIOContext
import dagger.hilt.android.lifecycle.HiltViewModel
import discut.manga.data.MangaAppDatabase
import discut.manga.data.category.Category
import discut.manga.data.manga.Manga
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val db: MangaAppDatabase,
    private val mangaProvider: IMangaProvider,
    private val sourceManager: ISourceManager
) : BaseViewModel<BookshelfState, BookshelfEvent, BookshelfEffect>() {
    override fun initialState(): BookshelfState = BookshelfState()

    init {
        sendEvent(BookshelfEvent.Init)
    }

    override suspend fun handleEvent(
        event: BookshelfEvent,
        state: BookshelfState
    ): BookshelfState {
        return when (event) {
            is BookshelfEvent.Init -> {
                withIOContext {
                    val categories =
                        DefaultCategories + db.categoryDao().getAll().sortedBy { it.order }
                    val shelfManga = subscribeShelfManga(categories)
                    state.copy(
                        categories = categories,
                        loadState = BookshelfState.LoadState.Loaded(shelfManga)
                    )
                }
            }

            else -> {state}
        }
    }

    private suspend fun subscribeShelfManga(categories: List<Category>): ShelfManga {
        val shelfManga = mutableMapOf<Category, StateFlow<List<Manga>>>()
        categories.forEach { category ->
            shelfManga[category] =
                mangaProvider.subscribeCategory(category.id).stateIn(CoroutineScope(Dispatchers.IO))
        }
        return shelfManga
    }

}

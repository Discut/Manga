package com.discut.manga.ui.bookshelf

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.source.ISourceManager
import com.discut.manga.util.withIOContext
import dagger.hilt.android.lifecycle.HiltViewModel
import discut.manga.data.MangaAppDatabase
import discut.manga.data.category.Category
import discut.manga.data.category.Category.Companion.UNCATEGORIZED_ID
import discut.manga.data.manga.Manga
import javax.inject.Inject

@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val db: MangaAppDatabase,
    private val sourceManager: ISourceManager
) : BaseViewModel<BookshelfState, BookshelfEvent, BookshelfEffect>() {
    override fun initialState(): BookshelfState = BookshelfState()

    override suspend fun handleEvent(
        event: BookshelfEvent,
        state: BookshelfState
    ): BookshelfState {
        return when (event) {
            is BookshelfEvent.Init -> {
                withIOContext {
                    val categories = listOf(DefaultCategory) + db.categoryDao().getAll()
                    val shelfManga = getShelfManga(categories)
                    state.copy(
                        categories = categories,
                        loadState = BookshelfState.LoadState.Loaded(shelfManga)
                    )
                }
            }
        }
    }

    private fun getShelfManga(categories: List<Category>): ShelfManga {
        val shelfManga = mutableMapOf<Category, List<Manga>>()
        categories.forEach { category ->
            val allMangaFromCategory = db.mangaDao().getAllByCategory(category.id)
            shelfManga[category] = allMangaFromCategory.filter { it.favorite }.toList()
        }
        return shelfManga
    }

}

val DefaultCategory = Category(
    id = UNCATEGORIZED_ID,
    name = "Default",
    order = -1
)
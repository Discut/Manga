package com.discut.manga.ui.bookshelf

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.source.ISourceManager
import com.discut.manga.util.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import discut.manga.data.MangaAppDatabase
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
    ): BookshelfState? {
        return when (event) {
            is BookshelfEvent.Init -> {
                launchIO {

                }
                state
            }
        }
    }

}
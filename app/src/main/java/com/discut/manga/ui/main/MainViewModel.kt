package com.discut.manga.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attribution
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreHoriz
import com.discut.core.mvi.BaseViewModel
import com.discut.manga.service.source.ISourceManager
import com.discut.manga.ui.bookshelf.BookshelfScreen
import com.discut.manga.ui.history.HistoryScreen
import com.discut.manga.ui.main.domain.MainEffect
import com.discut.manga.ui.main.domain.MainEvent
import com.discut.manga.ui.main.domain.MainState
import com.discut.manga.ui.main.domain.NavBarItem
import com.discut.manga.ui.more.MoreScreen
import com.discut.manga.ui.source.SourceScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sourceManager: ISourceManager
) :
    BaseViewModel<MainState, MainEvent, MainEffect>() {


    companion object {
        const val DEFAULT_SCREEN_ROUTE = "/books"
    }

    override fun initialState(): MainState {
        return MainState(
            navBarItems = listOf(
                NavBarItem("Books", "/books", Icons.Filled.Bookmarks) { BookshelfScreen() },
                NavBarItem("History", "/history", Icons.Filled.History) { HistoryScreen() },
                NavBarItem("Source", "/source", Icons.Filled.Attribution) {
                    SourceScreen(
                        sourceManager
                    )
                },
                NavBarItem("More", "more", Icons.Filled.MoreHoriz) { MoreScreen() }
            )
        )
    }

    override suspend fun handleEvent(event: MainEvent, state: MainState): MainState {
        return when (event) {
            is MainEvent.ClickNavigationItem -> {
                sendEffect(MainEffect.NavigateTo(event.navigationItem.route))
                state
            }
        }
    }

}
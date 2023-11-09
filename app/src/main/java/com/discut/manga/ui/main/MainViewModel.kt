package com.discut.manga.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attribution
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreHoriz
import com.discut.core.mvi.BaseViewModel
import com.discut.manga.ui.main.domain.MainEffect
import com.discut.manga.ui.main.domain.MainEvent
import com.discut.manga.ui.main.domain.MainState
import com.discut.manga.ui.main.domain.NavBarItem
import com.discut.manga.ui.more.MoreScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() :
    BaseViewModel<MainState, MainEvent, MainEffect>() {

    companion object {
        const val DEFAULT_SCREEN_ROUTE = "/books"
    }

    override fun initialState(): MainState {
        return MainState(
            navBarItems = listOf(
                NavBarItem("Books", "/books", Icons.Filled.Bookmarks) { MoreScreen() },
                NavBarItem("History", "/history", Icons.Filled.History) { MoreScreen() },
                NavBarItem("Search", "/search", Icons.Filled.Attribution) { MoreScreen() },
                NavBarItem("More", "/more", Icons.Filled.MoreHoriz) { MoreScreen() }
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
package com.discut.manga.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attribution
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreHoriz
import com.discut.core.mvi.BaseViewModel
import com.discut.manga.ui.main.data.NavBarItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() :
    BaseViewModel<MainState, MainEvent, MainEffect>() {
    override fun initialState(): MainState {
        return MainState(
            navBarItems = listOf(
                NavBarItem("Books", "/books", Icons.Filled.Bookmarks),
                NavBarItem("History", "/history", Icons.Filled.History),
                NavBarItem("Search", "/search", Icons.Filled.Attribution),
                NavBarItem("More", "/more", Icons.Filled.MoreHoriz)
            )
        )
    }

    override suspend fun handleEvent(event: MainEvent, state: MainState): MainState {
        return state
    }

}
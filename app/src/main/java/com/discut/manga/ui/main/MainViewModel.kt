package com.discut.manga.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attribution
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.lifecycle.viewModelScope
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
import com.discut.manga.util.get
import com.discut.manga.util.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import manga.core.preference.PreferenceManager
import manga.core.preference.SettingsPreference
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sourceManager: ISourceManager
) :
    BaseViewModel<MainState, MainEvent, MainEffect>() {

    private val settingsPreference = PreferenceManager.get<SettingsPreference>()


    companion object {
        const val DEFAULT_SCREEN_ROUTE = "/books"
    }

    init {
        viewModelScope.launchIO {
            settingsPreference.getNoTranceModeAsFlow().distinctUntilChanged().collect {
                sendEvent(MainEvent.RefreshNavigationItems)
            }
        }

    }

    override fun initialState(): MainState {
        return MainState(
            navBarItems = buildNavBarItems()
        )
    }

    override suspend fun handleEvent(event: MainEvent, state: MainState): MainState {
        return when (event) {
            is MainEvent.ClickNavigationItem -> {
                sendEffect(MainEffect.NavigateTo(event.navigationItem.route))
                state
            }

            is MainEvent.RefreshNavigationItems -> {
                state.copy(
                    navBarItems = buildNavBarItems()
                )
            }
        }
    }

    private fun buildNavBarItems(): List<NavBarItem> {
        return if (settingsPreference.getNoTranceMode()) {
            listOf(
                NavBarItem("Books", "/books", Icons.Filled.Bookmarks) { BookshelfScreen() },
                NavBarItem(
                    "History",
                    "/history",
                    Icons.Filled.History,
                    hide = true
                ) { HistoryScreen() },
                NavBarItem("Source", "/source", Icons.Filled.Attribution) {
                    SourceScreen(
                        sourceManager
                    )
                },
                NavBarItem("More", "more", Icons.Filled.MoreHoriz) { MoreScreen() }
            )
        } else {
            listOf(
                NavBarItem("Books", "/books", Icons.Filled.Bookmarks) { BookshelfScreen() },
                NavBarItem("History", "/history", Icons.Filled.History) { HistoryScreen() },
                NavBarItem("Source", "/source", Icons.Filled.Attribution) {
                    SourceScreen(
                        sourceManager
                    )
                },
                NavBarItem("More", "more", Icons.Filled.MoreHoriz) { MoreScreen() }
            )
        }
    }

}
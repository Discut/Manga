package com.discut.manga.ui.main

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import com.discut.manga.ui.main.domain.NavBarItem

data class MainState(val navBarItems: List<NavBarItem>) : UiState

sealed interface MainEvent : UiEvent {

    data class ClickNavigationItem(val navigationItem: NavBarItem) : MainEvent
}

sealed interface MainEffect : UiEffect {
    data class NavigateTo(val route: String) : MainEffect

    data class OpenAbout(val title: String) : MainEffect
}
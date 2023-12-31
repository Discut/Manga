package com.discut.manga.ui.main.domain

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState

data class MainState(val navBarItems: List<NavBarItem>) : UiState

sealed interface MainEvent : UiEvent {

    data class ClickNavigationItem(val navigationItem: NavBarItem) : MainEvent

    data object RefreshNavigationItems : MainEvent
}

sealed interface MainEffect : UiEffect {
    data class NavigateTo(val route: String) : MainEffect

    data class OpenAbout(val title: String) : MainEffect
}
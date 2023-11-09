package com.discut.manga.ui.reader.domain


import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import com.discut.manga.ui.main.domain.NavBarItem

data class ReaderActivityState(val navBarItems: List<NavBarItem>) : UiState

sealed interface ReaderActivityEvent : UiEvent {

    data class ClickNavigationItem(val navigationItem: NavBarItem) : ReaderActivityEvent
}

sealed interface ReaderActivityEffect : UiEffect
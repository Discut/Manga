package com.discut.manga.ui.more

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import com.discut.manga.ui.main.domain.NavBarItem

data class MoreScreenState(val enableSecurityMode: Boolean = false) : UiState

sealed interface MoreScreenEvent : UiEvent {

    data class ClickSecurityMode(val enable: Boolean) : MoreScreenEvent
}

sealed interface MoreScreenEffect : UiEffect {
    data class SecurityModeChange(val enable: Boolean) : MoreScreenEffect

}
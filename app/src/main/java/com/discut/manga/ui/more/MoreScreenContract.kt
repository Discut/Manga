package com.discut.manga.ui.more

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState

data class MoreScreenState(
    val enableSecurityMode: Boolean,
    val enableNoTranceMode: Boolean
) : UiState

sealed interface MoreScreenEvent : UiEvent {

    data class SecurityModeChanged(val enable: Boolean) : MoreScreenEvent

    data class NoTranceModeChanged(val enable: Boolean) : MoreScreenEvent

}

sealed interface MoreScreenEffect : UiEffect {

}
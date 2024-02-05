package com.discut.manga.ui.settings.browse

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import discut.manga.data.source.SourceRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class BrowseSettingsState(
    val hideAlreadyInstallExtension: StateFlow<Boolean>,
    val extensionRepos: StateFlow<List<SourceRepo>> = MutableStateFlow(emptyList()),
) : UiState

sealed interface BrowseSettingsEvent : UiEvent {

    data object Init: BrowseSettingsEvent
    data class HideAlreadyInstallExtensionChanged(val hide: Boolean) : BrowseSettingsEvent
}

sealed interface BrowseSettingsEffect : UiEffect
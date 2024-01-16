package com.discut.manga.ui.settings.download

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class DownloadSettingsState(
    val wifiOnly: StateFlow<Boolean>,
    val downloadDir: StateFlow<String> = MutableStateFlow(""),
    val downloadDirDefault: String = "",
    val downloadDirMap: Map<String, String> = emptyMap(),
    val downloadInterval: StateFlow<Int>
) : UiState


sealed interface DownloadSettingsEvent : UiEvent {

    data object Init : DownloadSettingsEvent
    data class WifiOnlyChanged(val wifiOnly: Boolean) : DownloadSettingsEvent

    data class DownloadIntervalChanged(val interval: Int) : DownloadSettingsEvent

    data class DownloadDirChanged(val dir: String) : DownloadSettingsEvent
}

sealed interface DownloadSettingsEffect : UiEffect
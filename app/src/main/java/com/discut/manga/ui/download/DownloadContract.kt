package com.discut.manga.ui.download

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import com.discut.manga.service.saver.download.model.Downloader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class DownloadState(
    val downloads: StateFlow<List<StateFlow<List<Downloader>>>> = MutableStateFlow(emptyList())
) : UiState

sealed interface DownloadEvent : UiEvent {
    data object Init : DownloadEvent
    data class Cancel(val download: Downloader) : DownloadEvent
}

sealed interface DownloadEffect : UiEffect
package com.discut.manga.ui.download

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import com.discut.manga.service.saver.download.model.Downloader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import manga.source.HttpSource

data class DownloadState(
    val downloads: StateFlow<Map<HttpSource, List<Downloader>>> = MutableStateFlow(emptyMap())
) : UiState

sealed interface DownloadEvent : UiEvent {
    data object Init : DownloadEvent
    data class Cancel(val download: Downloader) : DownloadEvent

    data class Pause(val download: Downloader) : DownloadEvent

    data class Start(val download: Downloader) : DownloadEvent

    data class Retry(val download: Downloader) : DownloadEvent

    data class UpdateDownloadList(val source: HttpSource, val orders: Map<Long, Double>) :
        DownloadEvent

    data object StartAllDownloads : DownloadEvent

    data object PauseAllDownloads : DownloadEvent

    data object CancelAllDownloads : DownloadEvent
}

sealed interface DownloadEffect : UiEffect
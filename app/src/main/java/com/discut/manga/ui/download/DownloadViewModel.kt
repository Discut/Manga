package com.discut.manga.ui.download

import androidx.lifecycle.viewModelScope
import com.discut.core.mvi.BaseViewModel
import com.discut.manga.service.saver.download.DownloadProvider
import com.discut.manga.util.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val downloadProvider: DownloadProvider
) :
    BaseViewModel<DownloadState, DownloadEvent, DownloadEffect>() {
    override fun initialState(): DownloadState = DownloadState()

    init {
        viewModelScope.launchIO {
            val downloads = downloadProvider.getAllDownloads()
                .stateIn(
                    CoroutineScope(
                        Dispatchers.IO
                    )
                )
            sendState {
                copy(
                    downloads = downloads
                )
            }
        }
    }

    override suspend fun handleEvent(event: DownloadEvent, state: DownloadState): DownloadState? {
        return when (event) {
            is DownloadEvent.Init -> {
                state.copy(
                    downloads = downloadProvider.getAllDownloads().stateIn(
                        CoroutineScope(
                            Dispatchers.IO
                        )
                    )
                )
            }

            is DownloadEvent.Cancel -> {
                downloadProvider.cancelDownload(event.download)
                state
            }

            is DownloadEvent.Pause -> {
                downloadProvider.pauseDownload(event.download)
                state
            }

            is DownloadEvent.Start -> {
                downloadProvider.startDownload(event.download)
                state
            }

            is DownloadEvent.Retry -> {
                downloadProvider.retryDownload(event.download)
                state
            }


            is DownloadEvent.UpdateDownloadList -> {
                downloadProvider.updateQueueOrder(event.source, event.orders)
                state
            }

            is DownloadEvent.StartAllDownloads -> {
                downloadProvider.startDownloads()
                state
            }

            is DownloadEvent.PauseAllDownloads -> {
                downloadProvider.pauseDownloads()
                state
            }

            is DownloadEvent.CancelAllDownloads -> {
                downloadProvider.cancelDownloads()
                state
            }

            else -> null
        }
    }
}
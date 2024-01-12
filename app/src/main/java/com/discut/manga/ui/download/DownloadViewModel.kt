package com.discut.manga.ui.download

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.service.saver.download.DownloadProvider
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
        sendEvent {
            DownloadEvent.Init
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

            else -> null
        }
    }
}
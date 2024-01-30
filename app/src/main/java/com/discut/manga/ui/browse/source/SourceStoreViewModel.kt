package com.discut.manga.ui.browse.source

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.service.source.ISourceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SourceStoreViewModel @Inject constructor(
    private val sourceManager: ISourceManager
) :
    BaseViewModel<SourceStoreState, SourceStoreEvent, SourceStoreEffect>() {
    override fun initialState(): SourceStoreState =
        SourceStoreState(loadState = LoadState.Loading)

    init {
        sendEvent(SourceStoreEvent.Init)
    }

    override suspend fun handleEvent(
        event: SourceStoreEvent,
        state: SourceStoreState
    ): SourceStoreState = when (event) {
        is SourceStoreEvent.Init -> {
            sourceManager.updateAllExtensionsList()
            state.copy(
                loadState = LoadState.Success(
                    extensionsStateFlow = sourceManager.allExtensionsFlow
                )
            )
        }

        is SourceStoreEvent.Refresh -> {
            sendEvent {
                SourceStoreEvent.Init
            }
            state.copy(
                loadState = LoadState.Loading
            )
        }

        is SourceStoreEvent.UninstallExtension -> {
            sourceManager.uninstall(event.extension)
            state
        }

        is SourceStoreEvent.InstallExtension -> {
            sourceManager.install(event.extension)
            state
        }
    }
}

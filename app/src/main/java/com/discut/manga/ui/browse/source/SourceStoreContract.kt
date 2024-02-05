package com.discut.manga.ui.browse.source

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import com.discut.manga.data.source.Extension
import kotlinx.coroutines.flow.StateFlow

data class SourceStoreState(
    val loadState: LoadState
) : UiState

sealed interface SourceStoreEvent : UiEvent {
    data object Init : SourceStoreEvent

    data object Refresh : SourceStoreEvent

    data class UninstallExtension(
        val extension: Extension.LocalExtension.Success
    ) : SourceStoreEvent

    data class InstallExtension(
        val extension: Extension.RemoteExtension
    ) : SourceStoreEvent

}

sealed interface SourceStoreEffect : UiEffect

sealed interface LoadState {
    data object Loading : LoadState
    data class Success(
        val extensionsStateFlow: StateFlow<List<Extension>>
    ) : LoadState

    data class Error(
        val msg: String?,
        val error: Throwable
    ) : LoadState
}
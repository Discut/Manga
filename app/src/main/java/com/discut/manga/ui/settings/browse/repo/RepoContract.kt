package com.discut.manga.ui.settings.browse.repo

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import discut.manga.data.source.SourceRepo
import kotlinx.coroutines.flow.StateFlow


data class RepoState(
    val repoStateFlow: StateFlow<List<SourceRepo>>
) : UiState

sealed interface RepoEvent : UiEvent {
    data object Init : RepoEvent

    data class DeleteRepo(val repo: SourceRepo) : RepoEvent

    data class AddRepo(val repo: SourceRepo) : RepoEvent
}

sealed interface RepoEffect : UiEffect {
    data class Error(val error: String) : RepoEffect
}
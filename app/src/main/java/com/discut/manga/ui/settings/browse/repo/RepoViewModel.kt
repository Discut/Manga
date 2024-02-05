package com.discut.manga.ui.settings.browse.repo

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.util.withIOContext
import dagger.hilt.android.lifecycle.HiltViewModel
import discut.manga.data.MangaAppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RepoViewModel @Inject constructor() :
    BaseViewModel<RepoState, RepoEvent, RepoEffect>() {

    private val repoDao by lazy {
        MangaAppDatabase.DB.sourceRepoDao()
    }

    init {
        sendEvent(RepoEvent.Init)
    }

    override fun initialState(): RepoState = RepoState(
        repoStateFlow = MutableStateFlow(emptyList())
    )

    override suspend fun handleEvent(event: RepoEvent, state: RepoState): RepoState =
        when (event) {
            is RepoEvent.Init -> {
                withIOContext {
                    state.copy(
                        repoStateFlow = repoDao.getAllAsFlow()
                            .stateIn(CoroutineScope(Dispatchers.IO))
                    )
                }
            }

            is RepoEvent.DeleteRepo -> {
                withIOContext {
                    repoDao.delete(event.repo)
                }
                state
            }

            is RepoEvent.AddRepo -> {
                withIOContext {
                    repoDao.insert(event.repo)
                }
                state
            }
        }
}
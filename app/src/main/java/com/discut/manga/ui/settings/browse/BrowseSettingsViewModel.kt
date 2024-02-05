package com.discut.manga.ui.settings.browse

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.preference.BrowsePreference
import com.discut.manga.util.get
import dagger.hilt.android.lifecycle.HiltViewModel
import discut.manga.data.MangaAppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import manga.core.preference.PreferenceManager
import javax.inject.Inject

@HiltViewModel
class BrowseSettingsViewModel @Inject constructor() :
    BaseViewModel<BrowseSettingsState, BrowseSettingsEvent, BrowseSettingsEffect>() {

    private val browsePreference = PreferenceManager.get<BrowsePreference>()

    private val sourceRepoDao by lazy {
        MangaAppDatabase.DB.sourceRepoDao()
    }

    init {
        sendEvent(BrowseSettingsEvent.Init)
    }

    override fun initialState(): BrowseSettingsState = BrowseSettingsState(
        hideAlreadyInstallExtension = MutableStateFlow(browsePreference.isHideAlreadyInstallExtension()),
    )


    override suspend fun handleEvent(
        event: BrowseSettingsEvent,
        state: BrowseSettingsState
    ): BrowseSettingsState = when (event) {
        is BrowseSettingsEvent.Init -> {
            state.copy(
                hideAlreadyInstallExtension = browsePreference.getHideAlreadyInstallExtensionAsFlow()
                    .stateIn(CoroutineScope(Dispatchers.IO)),
                extensionRepos = sourceRepoDao.getAllAsFlow()
                    .stateIn(CoroutineScope(Dispatchers.IO))
            )
        }

        else -> {
            state
        }
    }
}
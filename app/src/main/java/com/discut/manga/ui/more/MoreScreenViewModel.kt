package com.discut.manga.ui.more

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.util.get
import dagger.hilt.android.lifecycle.HiltViewModel
import manga.core.preference.PreferenceManager
import manga.core.preference.SettingsPreference
import javax.inject.Inject

@HiltViewModel
class MoreScreenViewModel @Inject constructor() :
    BaseViewModel<MoreScreenState, MoreScreenEvent, MoreScreenEffect>() {

    private val settingsPreference = PreferenceManager.get<SettingsPreference>()

    override fun initialState(): MoreScreenState {
        return MoreScreenState(
            enableSecurityMode = settingsPreference.enableSecurityMode()
        )
    }

    override suspend fun handleEvent(
        event: MoreScreenEvent,
        state: MoreScreenState
    ): MoreScreenState {
        return when (event) {
            is MoreScreenEvent.ClickSecurityMode -> {
                settingsPreference.setSecurityMode(event.enable)
                sendEffect(MoreScreenEffect.SecurityModeChange(event.enable))
                state.copy(
                    enableSecurityMode = event.enable
                )
            }
        }
    }
}
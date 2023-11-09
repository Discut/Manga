package com.discut.manga.ui.settings.security

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.ui.security.SecurityPreference
import com.discut.manga.ui.settings.security.domain.SecuritySettingsEffect
import com.discut.manga.ui.settings.security.domain.SecuritySettingsEvent
import com.discut.manga.ui.settings.security.domain.SecuritySettingsState
import com.discut.manga.util.get
import manga.core.preference.PreferenceManager
import manga.core.preference.SettingsPreference

class SecuritySettingsViewModel :
    BaseViewModel<SecuritySettingsState, SecuritySettingsEvent, SecuritySettingsEffect>() {
    private val settingsPreference = PreferenceManager.get<SettingsPreference>()
    private val securityPreference = PreferenceManager.get<SecurityPreference>()
    override fun initialState(): SecuritySettingsState {
        return SecuritySettingsState(
            enableSecurityMode = settingsPreference.enableSecurityMode(),
            enableAppLock = securityPreference.useAuthenticator(),
            enableHidePreview = securityPreference.enableHidePreview()
        )
    }

    override suspend fun handleEvent(
        event: SecuritySettingsEvent,
        state: SecuritySettingsState
    ): SecuritySettingsState {
        return when (event) {
            is SecuritySettingsEvent.ClickSecurityModeComponent -> {
                settingsPreference.setSecurityMode(event.enable)
                state.copy(enableSecurityMode = event.enable)
            }

            is SecuritySettingsEvent.ClickAppLockComponent -> {
                securityPreference.setAuthenticator(event.enable)
                state.copy(
                    enableAppLock = event.enable
                )
            }

            is SecuritySettingsEvent.ClickHidePreviewComponent -> {
                securityPreference.setHidePreview(event.enable)
                state.copy(
                    enableHidePreview = event.enable
                )
            }

        }
    }

}
package com.discut.manga.ui.settings.security.domain

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState

data class SecuritySettingsState(
    val enableSecurityMode: Boolean,
    val enableAppLock: Boolean,
    val enableHidePreview: Boolean
) : UiState

sealed interface SecuritySettingsEvent : UiEvent {
    data class ClickSecurityModeComponent(val enable: Boolean) : SecuritySettingsEvent
    data class ClickAppLockComponent(val enable: Boolean) : SecuritySettingsEvent
    data class ClickHidePreviewComponent(val enable: Boolean) : SecuritySettingsEvent
}

sealed interface SecuritySettingsEffect : UiEffect
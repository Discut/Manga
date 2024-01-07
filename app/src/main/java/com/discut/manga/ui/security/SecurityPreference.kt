package com.discut.manga.ui.security

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import manga.core.preference.AppPreference

class SecurityPreference(appPreference: SharedPreferences, flow: Flow<String?>) : AppPreference(appPreference, flow) {
    companion object {
        private const val KEY_USE_AUTHENTICATOR = "use_authenticator"
        private const val KEY_ENABLE_HIDE_PREVIEW = "enable_hide_preview"
    }

    fun useAuthenticator() =
        getValue(KEY_USE_AUTHENTICATOR, false)

    fun enableHidePreview() =
        getValue(KEY_ENABLE_HIDE_PREVIEW, false)

    fun setHidePreview(enable: Boolean) {
        getEdit().putBoolean(KEY_ENABLE_HIDE_PREVIEW, enable).apply()
    }

    fun setAuthenticator(enable: Boolean) {
        getEdit().putBoolean(KEY_USE_AUTHENTICATOR, enable).apply()
    }
}
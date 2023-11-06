package com.discut.manga.ui.security

import android.content.SharedPreferences
import manga.core.preference.AppPreference

class SecurityPreference(appPreference: SharedPreferences) : AppPreference(appPreference) {
    fun useAuthenticator() =
        getValue("use_authenticator", true)
}
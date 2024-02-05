package com.discut.manga.preference

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import manga.core.preference.AppPreference

class BrowsePreference(appPreference: SharedPreferences, flow: Flow<String?>) :
    AppPreference(appPreference, flow) {

        companion object{
            const val HIDE_ALREADY_INSTALL_EXTENSION = "hide_already_install_extension"
        }

    fun isHideAlreadyInstallExtension(): Boolean {
        return getValue(HIDE_ALREADY_INSTALL_EXTENSION, false)
    }

    fun getHideAlreadyInstallExtensionAsFlow(): Flow<Boolean> {
        return getValueAsFlow(HIDE_ALREADY_INSTALL_EXTENSION, false)
    }

    fun setHideAlreadyInstallExtension(hide: Boolean) {
        edit {
            putBoolean(HIDE_ALREADY_INSTALL_EXTENSION, hide)
        }
    }
}
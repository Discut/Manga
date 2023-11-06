package com.discut.manga.util

import com.discut.manga.App
import dagger.hilt.EntryPoints
import manga.core.preference.AppPreference
import manga.core.preference.PreferenceManager
import manga.core.preference.PreferenceManagerModule

inline fun <reified T : AppPreference> PreferenceManager.Companion.get() =
    EntryPoints.get(App.instance, PreferenceManagerModule::class.java).get()
        .getPreferences<T>()
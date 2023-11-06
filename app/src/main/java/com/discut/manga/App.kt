package com.discut.manga

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import manga.core.preference.PreferenceManager
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    companion object {
        lateinit var instance: Application
    }

    @Inject
    lateinit var appPreference: PreferenceManager

    override fun onCreate() {
        super.onCreate()
        appPreference.initManager()
        instance = this
    }
}
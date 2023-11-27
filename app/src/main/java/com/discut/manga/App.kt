package com.discut.manga

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import discut.manga.data.MangaAppDatabase
import managa.source.online.Baimangu
import manga.core.application.application
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
        // save application instance
        instance = this
        application = this
        // init module
        appPreference.initManager()
        MangaAppDatabase.init(this)
        // init end
    }
}
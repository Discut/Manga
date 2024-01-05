package com.discut.manga

import android.app.Application
import com.discut.manga.service.source.SourceManager
import dagger.hilt.android.HiltAndroidApp
import discut.manga.data.MangaAppDatabase
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

    @Inject
    lateinit var sourceManager: SourceManager

    override fun onCreate() {
        super.onCreate()
        // save application instance
        instance = this
        application = this
        // init module
        appPreference.initManager()
        sourceManager.initManager()
        MangaAppDatabase.init(this)
        // init end
    }
}

fun Any.getResourceString(resId: Int): String {
    return App.instance.getString(resId)
}
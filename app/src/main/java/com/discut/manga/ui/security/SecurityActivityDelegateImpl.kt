package com.discut.manga.ui.security

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.discut.manga.util.enableSecureScreen
import com.discut.manga.util.get
import com.discut.manga.util.startToActivity
import com.discut.manga.util.unableSecureScreen
import manga.core.preference.PreferenceManager
import manga.core.preference.SettingsPreference

class SecurityActivityDelegateImpl : SecurityActivityDelegate,
    DefaultLifecycleObserver {

    private lateinit var activity: AppCompatActivity
    private val securityPreference =
        PreferenceManager.get<SecurityPreference>()
    private val settingsPreference = PreferenceManager.get<SettingsPreference>()

    override fun registerSecurityActivity(activity: AppCompatActivity) {
        activity.lifecycle.addObserver(this)
        this.activity = activity
    }

    override fun onCreate(owner: LifecycleOwner) {
        if (settingsPreference.enableSecurityMode().not()) {
            return
        }
        if (securityPreference.enableHidePreview()) {
            activity.window.enableSecureScreen()
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        if (securityPreference.useAuthenticator().not()) {
            return
        }
        if (UnlockActivity.isAuthorized) {
            return
        }
        activity.window.unableSecureScreen()
        setApplicationLock()
    }

    override fun onPause(owner: LifecycleOwner) {
        if (isNeedLock().not()) {
            return
        }
        UnlockActivity.isAuthorized = false
    }

    private fun setApplicationLock() {
        if (isNeedLock().not()) {
            return
        }
        activity.startToActivity(UnlockActivity::class.java)
    }

    private fun isNeedLock(): Boolean {
        return securityPreference.useAuthenticator() && settingsPreference.enableSecurityMode()
    }

}


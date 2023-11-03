package com.discut.manga.ui.security

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.discut.manga.ui.security.util.enableSecureScreen

class SecurityActivityDelegateImpl : SecurityActivityDelegate,
    DefaultLifecycleObserver {

    private lateinit var activity: AppCompatActivity
    override fun registerSecurityActivity(activity: AppCompatActivity) {
        activity.lifecycle.addObserver(this)
        this.activity = activity
    }

    override fun onCreate(owner: LifecycleOwner) {
        activity.window.enableSecureScreen()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
    }


}
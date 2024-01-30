package com.discut.manga.receiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class ExtensionChangeEventDelegateImpl : ExtensionChangeEventDelegate, DefaultLifecycleObserver {

    private lateinit var activity: AppCompatActivity

    private val receiver = ExtensionsModifyReceiver()

    override fun registerExtensionChangeEventReceiver(activity: AppCompatActivity) {
        activity.lifecycle.addObserver(this)
        this.activity = activity
    }

    override fun onStart(owner: LifecycleOwner) {
        IntentFilter()
            .apply {
                addAction(Intent.ACTION_PACKAGE_ADDED)
                addAction(Intent.ACTION_PACKAGE_REMOVED)
                addAction(Intent.ACTION_PACKAGE_REPLACED)
            }
            .also {
                it.addDataScheme("package")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    activity.registerReceiver(
                        receiver, it,
                        Context.RECEIVER_NOT_EXPORTED
                    )
                } else {
                    activity.registerReceiver(receiver, it)
                }

            }
        super.onStart(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        activity.unregisterReceiver(receiver)
        super.onStop(owner)
    }

}
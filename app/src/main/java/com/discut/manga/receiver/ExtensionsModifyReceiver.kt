package com.discut.manga.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.discut.core.flowbus.FlowBus
import com.discut.manga.event.ExtensionChangeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ExtensionsModifyReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(Dispatchers.IO)
    companion object{
        private const val EXTENSION_PACKAGE_NAME = "manga.extension"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) {
            return
        }
        if (intent.dataString.isNullOrEmpty()) {
            return
        }
        val packageName = intent.dataString!!.substring(8)
        if (!packageName.contains(EXTENSION_PACKAGE_NAME)) {
            return
        }
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                FlowBus
                    .with<ExtensionChangeEvent>()
                    .post(scope, ExtensionChangeEvent.Added(packageName))
            }

            Intent.ACTION_PACKAGE_REMOVED -> {
                FlowBus
                    .with<ExtensionChangeEvent>()
                    .post(scope, ExtensionChangeEvent.Removed(packageName))
            }

            Intent.ACTION_PACKAGE_REPLACED -> {
                FlowBus
                    .with<ExtensionChangeEvent>()
                    .post(scope, ExtensionChangeEvent.Replace(packageName))
            }

            else -> {}
        }
    }
}
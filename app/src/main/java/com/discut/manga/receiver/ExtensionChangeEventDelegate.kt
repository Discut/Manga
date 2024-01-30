package com.discut.manga.receiver

import androidx.appcompat.app.AppCompatActivity

interface ExtensionChangeEventDelegate {

    fun registerExtensionChangeEventReceiver(activity: AppCompatActivity)
}
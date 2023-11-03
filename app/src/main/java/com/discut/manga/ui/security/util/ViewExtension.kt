package com.discut.manga.ui.security.util

import android.view.Window
import android.view.WindowManager

fun Window.enableSecureScreen() {
    setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
}

fun Window.unableSecureScreen() {
    clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
}

package com.discut.manga.util

import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.ui.platform.ComposeView
import com.discut.manga.theme.MangaTheme

fun Window.enableSecureScreen() {
    setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
}

fun Window.unableSecureScreen() {
    clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
}

inline fun ComponentActivity.setComposeContent(
    parent: CompositionContext? = null,
    crossinline content: @Composable () -> Unit
) {
    setContent {
        MangaTheme {
            content()
        }
    }
}


fun ComposeView.setComposeContent(content: @Composable () -> Unit) {
    setContent {
        MangaTheme {
            content()
        }
    }
}

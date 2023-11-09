package com.discut.manga.ui.util

import androidx.compose.runtime.Composable
import com.discut.manga.ui.main.MainScreen
import com.discut.manga.ui.settings.security.SecuritySettingsScreen

sealed class GlobalNavigationRoute {
    data class MainScreen(val route: String) : GlobalNavigationRoute()

    companion object
}

fun GlobalNavigationRoute.Companion.graph(): Map<String, @Composable () -> Unit> {
    return mapOf(
        "/main" to { MainScreen() },
        "/settings/security" to { SecuritySettingsScreen() }
    )
}
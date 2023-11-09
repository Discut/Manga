package com.discut.manga.ui.main.domain

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class NavBarItem(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val screen: @Composable () -> Unit
)
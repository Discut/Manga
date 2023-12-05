package com.discut.manga.components.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun getMaxHeightWithoutStatusBar(): Dp {
    val calculateTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    return (screenHeightDp - calculateTopPadding.value.toInt()).dp
}
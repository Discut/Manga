package com.discut.manga.components.indicator

import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppLinearIndicator(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    if (!isVisible) {
        return
    }
    LinearProgressIndicator(
        modifier = modifier
            .height(2.dp)
    )
}
package com.discut.manga.components.utils

import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun Modifier.maxHeightWithoutStatusBar(): Modifier =
    this.requiredHeightIn(max = getMaxHeightWithoutStatusBar())

package com.discut.manga.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp

object Padding{
    val Small = 4.dp
    val Default = 8.dp
    val Medium = 12.dp
    val Large = 16.dp
    val ExtraLarge = 24.dp
    val ExtraBigLarge = 32.dp
    val ExtraExtraBiggerLarge = 48.dp
}

val MaterialTheme.padding: Padding
    get() = Padding
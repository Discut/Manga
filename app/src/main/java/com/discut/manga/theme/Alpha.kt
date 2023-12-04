package com.discut.manga.theme

import androidx.compose.material3.MaterialTheme

object Alpha {
    val Disabled = 0.38f
    val Lowest = 0.6f
    val Normal = 0.7f
    val High = 0.87f
    val Highest = 1f
}

val MaterialTheme.alpha: Alpha
    get() = Alpha
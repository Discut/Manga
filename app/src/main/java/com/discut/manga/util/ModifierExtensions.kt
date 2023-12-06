package com.discut.manga.util

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

fun Modifier.addClick(onClick: (() -> Unit)? = null) = this.clickable(enabled = onClick != null) {
    onClick?.invoke()
}
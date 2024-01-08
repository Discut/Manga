package com.discut.manga.ui.browse.tab

import androidx.compose.runtime.Composable
import com.discut.manga.ui.browse.SourceViewModel

data class TabContent(
    val name: String,
    val content: @Composable (vm: SourceViewModel) -> Unit
)
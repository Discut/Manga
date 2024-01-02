package com.discut.manga.ui.source.tab

import androidx.compose.runtime.Composable
import com.discut.manga.ui.source.SourceViewModel

data class TabContent(
    val name: String,
    val content: @Composable (vm: SourceViewModel) -> Unit
)
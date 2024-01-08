package com.discut.manga.ui.browse.tab

import androidx.compose.runtime.Composable
import com.discut.manga.ui.browse.BrowseViewModel

data class TabContent(
    val name: String,
    val content: @Composable (vm: BrowseViewModel) -> Unit
)
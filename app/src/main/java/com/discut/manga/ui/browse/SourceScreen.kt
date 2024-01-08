package com.discut.manga.ui.browse

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.discut.manga.service.source.ISourceManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SourceScreen(
    sourceManager: ISourceManager,
    vm: SourceViewModel = hiltViewModel()
) {
    val rememberCoroutineScope = rememberCoroutineScope()
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "Source") },
            windowInsets = WindowInsets.captionBar
        )
    }) {
        SourceScreenImpl(vm = vm, modifier = Modifier.padding(it))
    }
}
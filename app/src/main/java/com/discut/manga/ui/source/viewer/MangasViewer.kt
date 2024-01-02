package com.discut.manga.ui.source.viewer

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.discut.manga.components.indicator.AppLinearIndicator
import com.discut.manga.theme.padding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangasViewer(
    modifier: Modifier = Modifier,
    vm: MangaViewerViewModel = hiltViewModel(),
    title: String = "Manga Viewer",
    sourceId: Long,
    queryKey: String,

    onBack: () -> Unit
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = sourceId) {
        vm.sendEvent(MangaViewerEvent.Init(sourceId, queryKey))
    }
    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 2.dp
            ) {
                Column {
                    TopAppBar(
                        title = {
                            Text(text = title)
                        },
                        // windowInsets = WindowInsets.captionBar,
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                    // chip tools
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = MaterialTheme.padding.Default),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.Default),
                    ) {

                    }
                }
            }

        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        AppLinearIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it),
            isVisible = state.isLoading
        )
        if (state.isLoading) {
            return@Scaffold
        }
        if (state.status is MangaViewerStatus.Error || state.status is MangaViewerStatus.Waiting) {
            return@Scaffold
        }
        val success = state.status as MangaViewerStatus.Success
        MangaViewerContent(
            modifier = Modifier.padding(it),
            snackbarHostState = snackbarHostState,
            mangaList = success.mangasFlow.collectAsLazyPagingItems()
        )
    }
}

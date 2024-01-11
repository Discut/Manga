package com.discut.manga.ui.browse

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.discut.manga.components.CustomModalBottomSheet
import com.discut.manga.event.NavigationEvent
import com.discut.manga.navigation.NavigationRoute
import com.discut.manga.ui.browse.source.SourceStoreSheet
import com.discut.manga.util.dispatchBy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BrowseScreen(
    vm: BrowseViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val state by vm.uiState.collectAsState()
    var isShowSourceStore by remember {
        mutableStateOf(false)
    }
    val sources = state.sourceItems.filterIsInstance<SourceItem.Custom>().getOrElse(0) {
        SourceItem.Nothing
    }.sources
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "Browse") },
            windowInsets = WindowInsets.captionBar
        )
    },
        floatingActionButton = {
            FloatingActionButton(onClick = { isShowSourceStore = true }) {
                Icon(imageVector = Icons.Outlined.LocalMall, contentDescription = "Store")
            }
        }
    ) {
        // SourceScreenImpl(vm = vm, modifier = Modifier.padding(it))
        BrowseScreen2Impl(
            modifier = Modifier.padding(it),
            sources = sources,
            onSourceClick = { sourceId ->
                NavigationEvent("mangasViewer/$sourceId").dispatchBy(scope)
            },
            onDownloadClock = {
                NavigationEvent(NavigationRoute.DownloadScreen.route).dispatchBy(scope)

            })
    }
    CustomModalBottomSheet(
        isShow = isShowSourceStore,
        onDismissRequest = {
            isShowSourceStore = false
        }
    ) {
        SourceStoreSheet(modifier = Modifier.fillMaxSize())
    }
}
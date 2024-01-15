package com.discut.manga.ui.download

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.discut.manga.components.scaffold.AppBarActions
import com.discut.manga.components.scaffold.SearchAppToolbar
import com.discut.manga.theme.alpha
import com.discut.manga.theme.padding
import com.discut.manga.ui.download.component.DownloadItem
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun DownloadScreen(
    vm: DownloadViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by vm.uiState.collectAsState()
    Scaffold(
        topBar = {
            SearchAppToolbar(
                isMainAppbar = false,
                titleContent = {
                    Text(
                        text = "Download",
                    )
                },
                actions = {
                    AppBarActions {
                        toOverflowAction {
                            title = "Cancel All"
                            onClick = onBack
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        val downloads by state.downloads.collectAsStateWithLifecycle()
        Column(modifier = Modifier.padding(paddingValues)) {
            downloads.forEach { it ->
                val downloaderList by it.collectAsStateWithLifecycle()
                var unrealDownloaderList by remember { mutableStateOf(downloaderList) }
                /*                LaunchedEffect(key1 = downloaderList) {
                                    unrealDownloaderList = downloaderList
                                }*/
                if (downloaderList.isEmpty()) {
                    return@forEach
                }
                Text(
                    text = downloaderList.first().source.name,
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.padding.Normal)
                        .alpha(MaterialTheme.alpha.Normal)
                )
                val reorderableState = rememberReorderableLazyListState(
                    onMove = { from, to ->
                        unrealDownloaderList = unrealDownloaderList.toMutableList().apply {
                            add(to.index, removeAt(from.index))
                        }
                        unrealDownloaderList.forEachIndexed { index, it ->
                            it.download = it.download.copy(order = index.toDouble())
                        }
                    },
                    onDragEnd = { _, _ ->
                        vm.sendEvent {
                            DownloadEvent.UpdateDownloadList
                        }
                    })
                val data by derivedStateOf {
                    unrealDownloaderList.toMutableStateList()
                }
                LazyColumn(
                    state = reorderableState.listState,
                    modifier = Modifier.reorderable(reorderableState),
                ) {
                    items(items = data, key = { it.download.id }) {
                        ReorderableItem(
                            reorderableState = reorderableState,
                            key = it.download.id,
                        ) { isDragging ->
                            val elevation by
                            animateDpAsState(if (isDragging) 4.dp else 0.dp, label = "")
                            DownloadItem(
                                modifier = Modifier
                                    .shadow(elevation),
                                downloader = it,
                                state = reorderableState,
                                onCancel = {
                                    vm.sendEvent {
                                        DownloadEvent.Cancel(it)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
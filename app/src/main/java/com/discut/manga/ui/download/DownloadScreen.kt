package com.discut.manga.ui.download

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    var queryKey by remember { mutableStateOf("") }
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
                            title = "Start All"
                            onClick = {
                                vm.sendEvent {
                                    DownloadEvent.StartAllDownloads
                                }
                            }
                        }
                        toOverflowAction {
                            title = "Pause All"
                            onClick = {
                                vm.sendEvent {
                                    DownloadEvent.PauseAllDownloads
                                }
                            }
                        }
                        toOverflowAction {
                            title = "Cancel All"
                            onClick = {
                                vm.sendEvent {
                                    DownloadEvent.CancelAllDownloads
                                }
                            }
                        }
                    }
                },
                defaultSearchKey = queryKey,
                onChangeSearchKey = {
                    queryKey = it
                },
                onBack = onBack
            )
        }
    ) { paddingValues ->
        val downloads by state.downloads.collectAsStateWithLifecycle()
        LaunchedEffect(key1 = downloads) {
            Log.d("DownloadScreen", downloads.toString())
        }
        Column(modifier = Modifier.padding(paddingValues)) {
            downloads.forEach { (source, downloads) ->
                if (downloads.isEmpty()) {
                    return@forEach
                }
                val sortedList = downloads.sortedBy { it.download.order }
                Text(
                    text = source.name,
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.padding.Normal)
                        .alpha(MaterialTheme.alpha.Normal)
                )
                var unrealDownloaderList by remember { mutableStateOf(sortedList) }
                LaunchedEffect(key1 = downloads) {
                    unrealDownloaderList = sortedList
                }
                val reorderableState = rememberReorderableLazyListState(
                    onMove = { from, to ->
                        unrealDownloaderList = unrealDownloaderList.toMutableList().apply {
                            add(to.index, removeAt(from.index))
                        }
                        Log.i(
                            "DownloadScreen", "before ${
                                unrealDownloaderList.map {
                                    "${it.chapter.name}:${it.download.order}  "
                                }
                            }"
                        )

                        unrealDownloaderList.forEachIndexed { index, it ->
                            it.download = it.download.copy(order = index.toDouble())
                        }
                        Log.i("DownloadScreen", unrealDownloaderList.map {
                            "${it.chapter.name}:${it.download.order}  "
                        }.toString())
                    },
                    onDragEnd = { _, _ ->
                        vm.sendEvent {
                            DownloadEvent.UpdateDownloadList(
                                source,
                                unrealDownloaderList.associate {
                                    it.download.id to it.download.order
                                }
                            )
                        }
                    })
                val data by derivedStateOf {
                    unrealDownloaderList
                        .filter {
                            it.chapter.name.contains(queryKey) || it.manga.title.contains(
                                queryKey
                            )
                        }.toMutableStateList()
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
                                },
                                onStart = {
                                    vm.sendEvent {
                                        DownloadEvent.Start(it)
                                    }
                                },
                                onPause = {
                                    vm.sendEvent {
                                        DownloadEvent.Pause(it)
                                    }
                                },
                                onRetry = {
                                    vm.sendEvent {
                                        DownloadEvent.Retry(it)
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
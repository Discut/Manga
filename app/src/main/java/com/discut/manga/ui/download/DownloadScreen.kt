package com.discut.manga.ui.download

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.discut.manga.components.preference.BasePreferenceComponent
import com.discut.manga.components.scaffold.AppBarActions
import com.discut.manga.components.scaffold.SearchAppToolbar
import com.discut.manga.theme.alpha
import com.discut.manga.theme.padding
import com.discut.manga.util.toPx

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(
    vm: DownloadViewModel = hiltViewModel()
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
                if (downloaderList.isEmpty()) {
                    return@forEach
                }
                Text(
                    text = downloaderList.first().source.name,
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.padding.Normal)
                        .alpha(MaterialTheme.alpha.Normal)
                )
                LazyColumn {
                    items(items = downloaderList, key = { it.download.id }) {
                        val positionalThreshold = 80.dp.toPx()
                        val dismissState = rememberDismissState(
                            positionalThreshold = {
                                positionalThreshold
                            },
                            confirmValueChange = {
                                it != DismissValue.DismissedToStart
                            }
                        )
                        SwipeToDismissBox(modifier = Modifier.animateItemPlacement(),
                            state = dismissState,
                            directions = setOf(DismissDirection.StartToEnd),
                            backgroundContent = {
                                val direction =
                                    dismissState.dismissDirection ?: return@SwipeToDismissBox
                                val color by animateColorAsState(
                                    when (dismissState.targetValue) {
                                        DismissValue.Default -> Color.LightGray
                                        DismissValue.DismissedToEnd -> Color.Red
                                        DismissValue.DismissedToStart -> Color.Red
                                    },
                                    label = "swipe color"
                                )
                                val alignment = when (direction) {
                                    DismissDirection.StartToEnd -> Alignment.CenterStart
                                    DismissDirection.EndToStart -> Alignment.CenterEnd
                                }
                                val icon = when (direction) {
                                    DismissDirection.StartToEnd -> Icons.Default.Delete
                                    DismissDirection.EndToStart -> Icons.Default.Done
                                }
                                val scale by animateFloatAsState(
                                    if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f,
                                    label = "swipe scale"
                                )

                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(color)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = alignment
                                ) {
                                    Icon(
                                        icon,
                                        contentDescription = "Localized description",
                                        modifier = Modifier.scale(scale)
                                    )
                                }
                            }) {
                            val progress by it.progressFlow.collectAsStateWithLifecycle(initialValue = 0)
                            var progressText by remember { mutableStateOf("") }
                            LaunchedEffect(key1 = progress) {
                                progressText = "${it.downloadedImages}/${it.pages?.size}"
                            }
                            Surface{
                                BasePreferenceComponent(
                                    title = it.chapter.name,
                                    iconWidget = {
                                        Icon(
                                            modifier = Modifier.padding(MaterialTheme.padding.Normal),
                                            imageVector = Icons.Outlined.Menu,
                                            contentDescription = "Drag"
                                        )
                                    },
                                    endWidget = {
                                        var expanded by remember { mutableStateOf(false) }
                                        IconButton(
                                            onClick = { expanded = !expanded },
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.MoreVert,
                                                contentDescription = "More",
                                            )
                                        }
                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false },
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text(text = "Stop") },
                                                onClick = { /*TODO*/ })
                                            DropdownMenuItem(
                                                text = { Text(text = "Cancel") },
                                                onClick = { /*TODO*/ })
                                        }
                                    },
                                    subWidget = {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = it.manga.title,
                                                maxLines = 1,
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.alpha(MaterialTheme.alpha.Normal)
                                            )

                                            Text(
                                                text = progressText,
                                                maxLines = 1,
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.alpha(MaterialTheme.alpha.Normal)
                                            )
                                        }
                                        LinearProgressIndicator(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(3.dp),
                                            progress = { progress.toFloat() / 100 })
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
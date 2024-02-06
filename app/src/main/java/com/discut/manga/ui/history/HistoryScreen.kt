package com.discut.manga.ui.history

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.discut.manga.components.scaffold.ItemActions
import com.discut.manga.components.scaffold.SearchAppToolbar
import com.discut.manga.event.NavigationEvent
import com.discut.manga.theme.padding
import com.discut.manga.ui.history.component.HistoryHeader
import com.discut.manga.ui.history.component.HistoryItem
import com.discut.manga.ui.history.component.ListSettingsSheet
import com.discut.manga.ui.reader.ReaderActivity
import com.discut.manga.util.postBy

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    vm: HistoryViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val state by vm.uiState.collectAsState()
    val histories by state.histories.collectAsState()
    val current = LocalContext.current
    var showListSettingsModalBottomSheet by remember {
        mutableStateOf(false)
    }
    Scaffold(topBar = {
        SearchAppToolbar(
            titleContent = {
                Text(
                    text = "History",
                )
            },
            actions = {
                ItemActions {
                    toOverflowTextAction {
                        title = "Clear all"
                        onClick = {
                            vm.sendEvent(HistoryEvent.ClearAll)
                        }
                    }
                    toOverflowTextAction {
                        title = "List settings"
                        onClick = {
                            showListSettingsModalBottomSheet = true
                        }
                    }
                }
            },
            onChangeSearchKey = {
                vm.sendEvent {
                    HistoryEvent.Search(it)
                }
            },
            windowInsets = WindowInsets.captionBar,
        )
    }) { it ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
        ) {
            items(items = histories, key = { h -> h.key }) {
                when (it) {
                    is HistoryAction.Header -> {
                        HistoryHeader(
                            header = it.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItemPlacement(
                                    animationSpec = tween(
                                        durationMillis = 500,
                                    )
                                )
                        )
                    }

                    is HistoryAction.Item -> {
                        HistoryItem(
                            history = it.history,
                            itemType = state.historyListLayout,
                            paddingValues = PaddingValues(
                                horizontal = MaterialTheme.padding.Normal,
                                vertical = MaterialTheme.padding.Default
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItemPlacement(
                                    animationSpec = tween(
                                        durationMillis = 500,
                                    )
                                ),
                            onDelete = {
                                vm.sendEvent(HistoryEvent.Remove(it.history))
                            }
                        ) {
                            ReaderActivity.startActivity(
                                current,
                                it.history.mangaId,
                                it.history.chapterId
                            ) { mangaId ->
                                NavigationEvent("mangaDetails/${mangaId}").postBy(scope)
                            }
                        }
                    }
                }
            }
        }
    }

    ListSettingsSheet(
        isShow = showListSettingsModalBottomSheet,
        historyListLayout = state.historyListLayout,
        onHistoryListLayoutChange = {
            vm.sendEvent(HistoryEvent.ChangeListLayout(it))
        },
        onDismissRequest = {
            showListSettingsModalBottomSheet = !showListSettingsModalBottomSheet
        }
    )
}
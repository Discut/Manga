package com.discut.manga.ui.download

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.discut.manga.components.scaffold.AppBarActions
import com.discut.manga.components.scaffold.SearchAppToolbar
import com.discut.manga.theme.alpha
import com.discut.manga.theme.padding
import com.discut.manga.ui.download.component.DownloadItem

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
                        DownloadItem(
                            modifier = Modifier.animateItemPlacement(),
                            downloader = it,
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
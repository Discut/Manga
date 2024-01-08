package com.discut.manga.ui.browse.tab

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.discut.manga.event.NavigationEvent
import com.discut.manga.ui.browse.BrowseViewModel
import com.discut.manga.ui.browse.component.SourceHeader
import com.discut.manga.ui.browse.component.SourceItem
import com.discut.manga.util.dispatchBy

@Composable
internal fun MangaSourceContent(vm: BrowseViewModel) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    LazyColumn {
        state.sourceItems.forEach { itemSet ->
            item {
                SourceHeader(header = itemSet.label)
            }
            itemSet.sources.forEach { source ->
                item {
                    SourceItem(source = source,
                        onClick = {
                            NavigationEvent("mangasViewer/${source.id}").dispatchBy(scope)
                        },
                        onLongClick = {

                        })
                }
            }

        }
    }
}

val MangaSourceTab = TabContent(
    name = "Source",
    content = { MangaSourceContent(it) }
)
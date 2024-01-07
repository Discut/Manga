package com.discut.manga.ui.bookshelf

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.discut.manga.event.NavigationEvent
import com.discut.manga.ui.bookshelf.component.BookshelfPage
import com.discut.manga.util.postBy

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookshelfScreenImpl(
    vm: BookshelfViewModel,
    state: BookshelfState,
    pagerState: PagerState
) {
    val context = LocalContext.current
    val rememberCoroutineScope = rememberCoroutineScope()
    if (state.loadState !is BookshelfState.LoadState.Loaded) {
        return
    }
    val shelfManga = state.loadState.shelfManga
    /*    Column {
            TextPreferenceComponent(title = "进入Reader") {
                ReaderActivity.startActivity(context, -3618642300592193536L, -3686196295002750976L)
            }*/
    /*        LazyColumn {
                state..forEach {
                    item {
                        TextPreferenceComponent(title = it.title, subTitle = it.url) {
                            NavigationEvent("mangaDetails/${it.id}").postBy(androidx.compose.runtime.rememberCoroutineScope)
                        }
                    }
                }
            }*/// }
    BookshelfPage(
        state = pagerState,
        getBooks = {
            shelfManga[state.categories[it]]?.collectAsState()?.value ?: emptyList()
        },
        onBookClick = {
            NavigationEvent("mangaDetails/${it.id}").postBy(rememberCoroutineScope)
        })


}
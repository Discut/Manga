package com.discut.manga.ui.bookshelf

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun BookshelfScreen(
    vm: BookshelfViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    //val coercedCurrentPage = remember { currentPage().coerceAtMost(categories.lastIndex) }
    val pagerState = rememberPagerState {
        state.categories.size
    }
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = state.categories.getOrNull(pagerState.currentPage)?.name ?: "书架",
                )
            },
            actions = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search")
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = "")
                }
            },
            windowInsets = WindowInsets.captionBar,
        )
    }) {
        Box(modifier = Modifier.padding(it)) {
            when (state.loadState) {
                is BookshelfState.LoadState.Error -> TODO()
                is BookshelfState.LoadState.Loaded -> BookshelfScreenImpl(
                    vm = vm,
                    state = state,
                    pagerState = pagerState
                )

                BookshelfState.LoadState.Waiting -> {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                    )
                }

                else -> {}
            }
        }
    }

}
package com.discut.manga.ui.browse

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.discut.manga.util.launchUI

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SourceScreenImpl(modifier: Modifier = Modifier, vm: SourceViewModel) {
    val scope = rememberCoroutineScope()
    val state by vm.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState { state.tabs.size }
    Column(modifier = modifier) {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            state.tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launchUI { pagerState.animateScrollToPage(index) } },
                    text = { Text(text = tab.name) },
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            verticalAlignment = Alignment.Top,
        ) {
            state.tabs[it].content(vm)
        }
    }
}
package com.discut.manga.ui.bookshelf.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import discut.manga.data.manga.Manga

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookshelfPage(
    state: PagerState,

    onBookClick: (Manga) -> Unit,
    getBooks: (Int) -> List<Manga>
) {
    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Top,
        state = state
    ) {
        if (it !in ((state.currentPage - 1)..(state.currentPage + 1))) {
            // To make sure only one offscreen page is being composed
            return@HorizontalPager
        }
        val books = getBooks(it)
        LazyBookshelfVerticalGrid(
            items = books,
            onBookClick = onBookClick
        )
    }
}
package com.discut.manga.ui.bookshelf.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.discut.manga.components.domain.toMangaCoverInfo
import com.discut.manga.components.manga.MangaCover
import discut.manga.data.manga.Manga

@Composable
fun LazyBookshelfVerticalGrid(
    modifier: Modifier = Modifier,
    items: List<Manga>
) {
    LazyVerticalGrid(columns = GridCells.Fixed(4)) {
        items(items.size) {
            Column {
                MangaCover.BOOK(info = items[it].toMangaCoverInfo())
            }
        }
    }
}
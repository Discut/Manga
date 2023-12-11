package com.discut.manga.ui.bookshelf.component

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.discut.manga.components.domain.toMangaCoverInfo
import discut.manga.data.manga.Manga

@Composable
fun LazyBookshelfVerticalGrid(
    modifier: Modifier = Modifier,
    items: List<Manga>,
    onBookClick: (Manga) -> Unit
) {
    LazyVerticalGrid(columns = GridCells.Fixed(4)) {
        items(items.size) {
            BookItem(info = items[it].toMangaCoverInfo(),
                onClick = { onBookClick(items[it]) })
        }
    }
}
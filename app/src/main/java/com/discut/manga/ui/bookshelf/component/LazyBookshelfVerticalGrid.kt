package com.discut.manga.ui.bookshelf.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.discut.manga.components.domain.toMangaCoverInfo
import com.discut.manga.theme.padding
import discut.manga.data.manga.Manga

@Composable
fun LazyBookshelfVerticalGrid(
    modifier: Modifier = Modifier,
    items: List<Manga>,
    onBookClick: (Manga) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.Default),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.Default),
        contentPadding = PaddingValues(MaterialTheme.padding.Default)
    ) {
        items(items.size) {
            LooseBookItem(info = items[it].toMangaCoverInfo(),
                onClick = { onBookClick(items[it]) })
        }
    }
}
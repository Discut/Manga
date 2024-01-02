package com.discut.manga.ui.source.viewer.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.discut.manga.components.domain.toMangaCoverInfo
import com.discut.manga.theme.padding
import com.discut.manga.ui.bookshelf.component.BookItem
import discut.manga.data.manga.Manga
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun LooseGridLayout(
    modifier: Modifier = Modifier,
    mangaList: LazyPagingItems<StateFlow<Manga>>,

    onBookClick: (Manga) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(MaterialTheme.padding.Default),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (mangaList.loadState.prepend is LoadState.Loading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                LoadingMangasItem()
            }
        }
        items(count = mangaList.itemCount) { index ->
            val manga by mangaList[index]?.collectAsState() ?: return@items
            BookItem(info = manga.toMangaCoverInfo(),
                onClick = { onBookClick(manga) })
        }
    }
}
package com.discut.manga.ui.bookshelf.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.discut.manga.components.domain.MangaCoverInfo
import com.discut.manga.components.manga.MangaCover

@Composable
fun LooseBookItem(
    modifier: Modifier = Modifier,
    info: MangaCoverInfo,
    onClick: () -> Unit
) {
    Column(modifier = modifier
        .fillMaxSize()
        .clickable { onClick() }) {
        MangaCover.BOOK(info = info)
        Text(text = info.title, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
    }
}
package com.discut.manga.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun MangaPlaceholderCover(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Title",
            modifier = Modifier.rotate(45f)
        )
        Text(text = "Subtitle",modifier = Modifier)
    }
}

@Composable
@Preview
fun MangaPlaceholderCoverPreview() {
    MangaPlaceholderCover(
        modifier = Modifier.height(300.dp).width(150.dp).background(Color.Blue)
    )
}
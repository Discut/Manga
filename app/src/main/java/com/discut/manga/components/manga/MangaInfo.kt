package com.discut.manga.components.manga

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.discut.manga.theme.alpha

@Composable
fun MangaInfo(
    modifier: Modifier = Modifier,
    title: String,
    author: String,
    artist: String,
    source: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(MaterialTheme.alpha.Normal),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            TextWithIcon(text = author) {
                Icon(
                    imageVector = Icons.Filled.PersonOutline,
                    contentDescription = "Author",
                    modifier = it
                )
            }
            TextWithIcon(text = artist) {
                Icon(
                    imageVector = Icons.Filled.Brush,
                    contentDescription = "Pinter",
                    modifier = it
                )
            }
            TextWithIcon(text = source) {
                Icon(
                    imageVector = Icons.Filled.Storage,
                    contentDescription = "Source",
                    modifier = it
                )
            }
        }
    }
}

@Composable
internal fun TextWithIcon(
    modifier: Modifier = Modifier,
    text: String,
    icon: @Composable (modifier: Modifier) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
        )
        icon(Modifier.size(16.dp))
    }
}
package com.discut.manga.components.manga

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            modifier = Modifier
                .padding(top = 8.dp, end = 8.dp, bottom = 8.dp),
            fontSize = 26.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(
                modifier = Modifier
                    .alpha(MaterialTheme.alpha.Normal),
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
            LikeButton {

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
        icon(Modifier.size(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Composable
internal fun LikeButton(
    onClick: () -> Unit
) {
    IconButton(onClick = { onClick() }) {
        Icon(imageVector = Icons.Outlined.FavoriteBorder, contentDescription = "Like")
    }
}
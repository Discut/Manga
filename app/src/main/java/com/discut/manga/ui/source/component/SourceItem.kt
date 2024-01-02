package com.discut.manga.ui.source.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import com.discut.manga.theme.alpha
import com.discut.manga.theme.padding
import managa.source.Source

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SourceItem(
    modifier: Modifier = Modifier,
    source: Source,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(
                horizontal = MaterialTheme.padding.Normal,
                vertical = MaterialTheme.padding.Default
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SourceIcon(source = source)
        Column(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.padding.Normal)
                .weight(1f),
        ) {
            Text(
                text = source.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                modifier = Modifier.alpha(MaterialTheme.alpha.Normal),
                text = source.language,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

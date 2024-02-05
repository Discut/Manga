package com.discut.manga.ui.browse.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import com.discut.manga.event.NavigationEvent
import com.discut.manga.theme.alpha
import com.discut.manga.theme.padding
import com.discut.manga.util.postBy
import manga.source.ConfigurationSource
import manga.source.Source

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
                vertical = MaterialTheme.padding.Default,
                horizontal = MaterialTheme.padding.Normal
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SourceIcon(source = source)
            Column(
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.padding.Normal),
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
        if (source is ConfigurationSource) {
            val scope = rememberCoroutineScope()
            IconButton(onClick = {
                NavigationEvent("sourcePreference/${source.id}").postBy(scope)
            }) {
                Icon(imageVector = Icons.Outlined.Settings, contentDescription = "settings")
            }
        }
    }
}

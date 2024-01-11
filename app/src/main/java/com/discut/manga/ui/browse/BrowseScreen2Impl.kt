package com.discut.manga.ui.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Source
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.discut.manga.theme.padding
import com.discut.manga.ui.browse.component.SourceItem
import discut.manga.source.local.LocalSource
import managa.source.Source

@Composable
internal fun <S : Source> BrowseScreen2Impl(
    modifier: Modifier = Modifier,
    sources: List<S>,
    onSourceClick: (Long) -> Unit,
    onDownloadClock: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        BrowserActionButtons(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.padding.Normal),
            onLocalSourceClock = { onSourceClick(LocalSource.ID) },
            onDownloadClock = onDownloadClock
        )
        Text(
            text = "Source",
            modifier = Modifier.padding(
                vertical = MaterialTheme.padding.Normal,
                horizontal = MaterialTheme.padding.Normal
            ),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
            ),
        )
        LazyColumn {
            items(items = sources) {
                SourceItem(source = it,
                    onClick = {
                        onSourceClick(it.id)
                    },
                    onLongClick = {

                    })
            }
        }
    }
}

@Composable
private fun BrowserActionButtons(
    modifier: Modifier = Modifier,
    onLocalSourceClock: () -> Unit,
    onDownloadClock: () -> Unit
) {
    val fieldColor: Color = MaterialTheme.colorScheme.primary
    val iconSize: Dp = 20.dp // default is 24.dp from Icon
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        FilledTonalButton(modifier = Modifier.weight(1f), onClick = onLocalSourceClock) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Outlined.Source,
                    contentDescription = "Local source",
                    tint = fieldColor
                )
                Text(text = "Local", color = fieldColor)
            }
        }
        Spacer(modifier = Modifier.width(MaterialTheme.padding.Normal))
        FilledTonalButton(modifier = Modifier.weight(1f), onClick = onDownloadClock) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Outlined.Download,
                    contentDescription = "Download",
                    tint = fieldColor
                )
                Text(text = "Download", color = fieldColor)
            }
        }
    }
}
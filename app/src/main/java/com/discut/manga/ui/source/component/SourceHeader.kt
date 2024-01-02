package com.discut.manga.ui.source.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.discut.manga.theme.padding

@Composable
fun SourceHeader(
    modifier: Modifier = Modifier,
    header: String,
) {
    Text(
        text = header,
        modifier = modifier
            .padding(
                horizontal = MaterialTheme.padding.Normal,
                vertical = MaterialTheme.padding.Default
            ),
        style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
        ),
    )
}
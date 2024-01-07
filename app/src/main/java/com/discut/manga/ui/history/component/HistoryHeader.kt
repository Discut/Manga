package com.discut.manga.ui.history.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.discut.manga.theme.padding

@Composable
fun HistoryHeader(
    modifier: Modifier = Modifier,
    header: String,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    Box(modifier = Modifier.padding(paddingValues)) {
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

}
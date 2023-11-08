package com.discut.manga.components.preference

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.discut.manga.theme.MangaTheme

@Composable
fun HeaderPreferenceGroupComponent(
    modifier: Modifier = Modifier,
    header: String,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, top = 14.dp),
    ) {
        Text(
            text = header,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    name = "Light"
)
private fun HeaderPreferenceComponentPreview() {
    MangaTheme {
        HeaderPreferenceGroupComponent(
            modifier = Modifier,
            header = "This is a HeaderPreferenceComponent."
        )
    }
}
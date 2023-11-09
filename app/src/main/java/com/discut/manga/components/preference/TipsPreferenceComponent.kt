package com.discut.manga.components.preference

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.discut.manga.theme.MangaTheme

@Composable
fun TipsPreferenceComponent(text: String) {
    Column(
        modifier = Modifier
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp,
            )
            .alpha(0.78f),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    name = "Light"
)
fun TipsPreferenceComponentPreview() {
    MangaTheme {
        TipsPreferenceComponent(text = "Oooops!!! Something went wrong.")
    }
}
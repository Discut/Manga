package com.discut.manga.ui.reader.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.discut.manga.theme.padding

@Composable
fun DefaultSpacer() {
    Spacer(modifier = Modifier.height(MaterialTheme.padding.Normal))
}

@Composable
fun Label(
    label: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        modifier = modifier.padding(
            horizontal = MaterialTheme.padding.Normal,
        ),
        color = MaterialTheme.colorScheme.primary
    )
}
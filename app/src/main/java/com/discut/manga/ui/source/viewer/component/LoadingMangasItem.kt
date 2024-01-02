package com.discut.manga.ui.source.viewer.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.discut.manga.theme.padding

@Composable
internal fun LoadingMangasItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.padding.Normal),
        horizontalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
    }
}
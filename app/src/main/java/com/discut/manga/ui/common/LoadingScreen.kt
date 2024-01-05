package com.discut.manga.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.discut.manga.theme.padding

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    placeholderText: String = ""
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
        if (placeholderText.isNotEmpty()) {
            Text(text = placeholderText,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = MaterialTheme.padding.Default)
            )
        }
    }
}

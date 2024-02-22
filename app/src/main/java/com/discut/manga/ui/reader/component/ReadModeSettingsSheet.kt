package com.discut.manga.ui.reader.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.discut.manga.theme.padding

@Composable
fun ReadModeSettingsSheet(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        DefaultSpacer()
        Label(label = "Mode")
        DefaultSpacer()
        ReaderModeSelector(
            modifier = Modifier.padding(horizontal = MaterialTheme.padding.Normal),
            onActive = {})
        DefaultSpacer()


    }
}
package com.discut.manga.ui.history

import android.view.LayoutInflater
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

import com.discut.manga.R
import com.discut.manga.components.manga.MangaInfoBox

@Composable
fun HistoryScreen() {
    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically  // 垂直居中
    ) {
        MangaInfoBox(
            char1 = "芙",
            char2 = "莲"
        )
    }
}
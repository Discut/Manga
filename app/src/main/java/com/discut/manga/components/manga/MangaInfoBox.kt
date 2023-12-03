package com.discut.manga.components.manga

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.widget.TextView
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.discut.manga.R
import com.discut.manga.theme.MangaTheme

@SuppressLint("InflateParams")
@Composable
fun MangaInfoBox(
    char1: String,
    char2: String,
) {
    val value = MaterialTheme.typography.titleLarge.fontSize.value
    Row(modifier = Modifier.height(180.dp)) {
        AndroidView(modifier = Modifier
            .fillMaxHeight()
            .weight(1.25f), factory = {
            val view =
                LayoutInflater.from(it).inflate(R.layout.cover_layout, null)
            val char1View = view.findViewById<TextView>(R.id.cover_text_1)
            val char2View = view.findViewById<TextView>(R.id.cover_text_2)
            char1View.textSize = value * 8
            char2View.textSize = value * 13
            char1View.text = char1
            char2View.text = char2
            view
        })
        Row(
            modifier = Modifier
                .weight(2.75f)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically  // 垂直居中
        ) {
            Text(
                text = "Title",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
@Preview
fun MangaInfoBoxPreview() {
    MangaTheme {
        MangaInfoBox(
            char1 = "A",
            char2 = "B",
        )
    }
}
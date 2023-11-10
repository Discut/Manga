package com.discut.manga.ui.bookshelf

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.discut.manga.components.preference.TextPreferenceComponent
import com.discut.manga.ui.reader.ReaderActivity

@Composable
fun BookshelfScreen() {
    val context = LocalContext.current
    TextPreferenceComponent(title = "进入Reader") {
        ReaderActivity.startActivity(context)
    }
}
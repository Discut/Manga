package com.discut.manga.ui.reader.viewer.vertical

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.discut.manga.ui.reader.page.PageTransitionView
import com.discut.manga.ui.reader.domain.ReaderPage

class VerticalTransitionHolder(
    private val context: Context,
    private val content: View
) : RecyclerView.ViewHolder(content) {
    fun bind(readerPage: ReaderPage.ChapterTransition) {
        if (content is PageTransitionView) {
            content.bind(readerPage)
        }
    }
}
package com.discut.manga.ui.reader.viewer

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.discut.manga.ui.reader.viewer.domain.ReaderPage

class RecyclerTransitionHolder(
    private val context: Context,
    private val content: View
) : RecyclerView.ViewHolder(content) {
    fun bind(readerPage: ReaderPage.ChapterTransition) {
        if (content is PageTransitionView) {
            content.bind(readerPage)
        }
    }
}
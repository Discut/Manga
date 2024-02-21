package com.discut.manga.ui.reader.page

import android.content.Context
import android.util.AttributeSet
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AbstractComposeView
import com.discut.manga.theme.MangaTheme
import com.discut.manga.ui.reader.component.ChapterTransition
import com.discut.manga.ui.reader.domain.ReaderPage

open class PageTransitionView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    AbstractComposeView(context, attrs), IPageView {

    private var transitionDate: ReaderPage.ChapterTransition? by mutableStateOf(null)

    init {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    fun bind(transition: ReaderPage.ChapterTransition) {
        transitionDate = transition
    }

    @Composable
    override fun Content() {
        if (transitionDate == null) {
            return
        }
        MangaTheme {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.bodySmall,
                LocalContentColor provides MaterialTheme.colorScheme.onBackground,
            ) {
                ChapterTransition(chapterTransition = transitionDate!!)
            }
        }
    }

    override fun bind(readerPage: ReaderPage) {
        if (readerPage is ReaderPage.ChapterTransition) {
            bind(readerPage)
        }
    }

}
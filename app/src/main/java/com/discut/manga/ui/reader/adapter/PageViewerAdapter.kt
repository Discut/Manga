package com.discut.manga.ui.reader.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.discut.manga.ui.reader.viewer.ChapterPageHolder
import com.discut.manga.ui.reader.viewer.domain.ReaderPage

class PageViewerAdapter(context: Context, readerPages: List<ReaderPage>) : IPagesViewAdapter,
    PagerAdapter() {
    var readerPages: MutableList<ReaderPage> = readerPages.toMutableList()
        private set

    override fun getCount(): Int {
        return readerPages.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun createPageView(container: ViewGroup, position: Int): View {
        return when (val page = readerPages[position]) {
            is ReaderPage.ChapterPage -> {
                ChapterPageHolder(container.context, page).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
            }

            is ReaderPage.ChapterTransition -> TextView(container.context).apply {

            }

            // else -> throw NotImplementedError("Unsupported page type: $page")
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        val view = obj as View
        destroyPageView(container, position, view)
        container.removeView(view)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = createPageView(container, position)
        container.addView(view)
        return view
    }

    override fun destroyPageView(container: ViewGroup, position: Int, view: View) {
        // TODO("Not yet implemented")
    }

    override fun getItemPosition(obj: Any): Int {
        val position = readerPages.indexOf(obj as ReaderPage)
        if (position != -1) {
            return position
        }
        return POSITION_NONE
    }
}
package com.discut.manga.ui.reader.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.discut.manga.ui.reader.viewer.ChapterPageHolder
import com.discut.manga.ui.reader.viewer.IPageView
import com.discut.manga.ui.reader.viewer.PageTransitionView
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

    override fun createPageView(container: ViewGroup, type: IPagesViewAdapter.PageType): View {
        return when (type) {
            IPagesViewAdapter.PageType.PAGE_VIEW -> {
                // ChapterPageHolder(container.context, page).apply {
                ChapterPageHolder(container.context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }

            IPagesViewAdapter.PageType.TRANSITION_VIEW -> PageTransitionView(container.context)

            // else -> throw NotImplementedError("Unsupported page type: $page")
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        val view = obj as View
        destroyPageView(container, position, view)
        container.removeView(view)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = createPageView(container, readerPages[position])
        /*if (view is ChapterPageHolder && readerPages[position] is ReaderPage.ChapterPage) {
            view.bind(readerPages[position] as ReaderPage.ChapterPage)
        }*/
        if (view is IPageView) {
            view.bind(readerPages[position])
        }
        container.addView(view)
        return view
    }

    override fun destroyPageView(container: ViewGroup, position: Int, view: View) {
        // TODO("Not yet implemented")
    }

    override fun setPages(pages: List<ReaderPage>) {
        readerPages = pages.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemPosition(obj: Any): Int {
        if (obj is ReaderPage.ChapterPage) {
            val position = readerPages.indexOf(obj)
            if (position != -1) {
                return position
            }
        }
/*        val position = readerPages.indexOf(obj)
        if (position != -1) {
            return position
        }*/
        return POSITION_NONE
    }
}
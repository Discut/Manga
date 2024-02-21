package com.discut.manga.ui.reader.viewer.horizontal

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.discut.manga.ui.reader.page.IPageView
import com.discut.manga.ui.reader.domain.ReaderPage
import com.discut.manga.ui.reader.viewer.IPagesViewAdapter

class HorizontalPageViewerAdapter(readerPages: List<ReaderPage>) : IPagesViewAdapter,
    PagerAdapter() {
    var readerPages: MutableList<ReaderPage> = readerPages.toMutableList()
        private set

    /**
     * Cache of page view, it is used to smooth transition between pages.
     * When chapter changes, the first page is got from the cache.
     */
    private val pageViewCache: MutableMap<ReaderPage, View> = mutableMapOf()
    private var isNeedClearPageViewCache = false

    override fun getCount(): Int {
        return readerPages.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun createPageView(container: ViewGroup, type: IPagesViewAdapter.PageType): View {
        return when (type) {
            IPagesViewAdapter.PageType.PAGE_VIEW ->
                HorizontalPageHolder(container.context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }

            IPagesViewAdapter.PageType.TRANSITION_VIEW -> HorizontalTransitionHolder(container.context)
        }
    }


    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        val view = obj as View
        destroyPageView(container, position, view)
        container.removeView(view)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val readerPage = readerPages[position]
        val view = if (pageViewCache[readerPage] == null) {
            createPageView(container, readerPage).apply {
                if (this is IPageView) {
                    bind(readerPage)
                }
                pageViewCache[readerPage] = this
            }
        } else pageViewCache[readerPage]!!

        container.addView(view)

        if (isNeedClearPageViewCache) {
            pageViewCache.clear()
            isNeedClearPageViewCache = false
        }

        return view
    }

    override fun destroyPageView(container: ViewGroup, position: Int, view: View) {
        // TODO("Not yet implemented")
    }

    override fun setPages(pages: List<ReaderPage>) {
        readerPages = pages.toMutableList()
        notifyDataSetChanged()

        isNeedClearPageViewCache = true
    }

    override fun getItemPosition(obj: Any): Int {
        if (obj is ReaderPage.ChapterPage) {
            val position = readerPages.indexOf(obj)
            if (position != -1) {
                return position
            }
        }
        return POSITION_NONE
    }
}
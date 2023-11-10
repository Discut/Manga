package com.discut.manga.ui.reader.viewer

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.discut.manga.ui.reader.viewer.base.BasePageViewerAdapter
import com.discut.manga.ui.reader.viewer.domain.PageType

class PageViewerAdapter : BasePageViewerAdapter() {

    var pages: MutableList<PageType> = mutableListOf(
    )
        private set

    override fun getCount(): Int {
        return pages.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun createPageView(container: ViewGroup, position: Int): View {
        return when (val page = pages[position]) {
            is PageType.ChapterPage -> {
                ChapterPageHolder(container.context, page).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }

            is PageType.ChapterTransition -> TextView(container.context).apply {

            }

            else -> throw NotImplementedError("Unsupported page type: $page")
        }
    }

    override fun destroyPageView(container: ViewGroup, position: Int, view: View) {
        TODO("Not yet implemented")
    }

    override fun getItemPosition(obj: Any): Int {
        val position = pages.indexOf(obj as PageType)
        if (position != -1) {
            return position
        }
        return POSITION_NONE
    }
}
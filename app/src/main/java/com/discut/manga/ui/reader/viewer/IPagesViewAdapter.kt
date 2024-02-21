package com.discut.manga.ui.reader.viewer

import android.view.View
import android.view.ViewGroup
import com.discut.manga.ui.reader.domain.ReaderPage

interface IPagesViewAdapter {
    enum class PageType {
        PAGE_VIEW, TRANSITION_VIEW
    }

    fun createPageView(container: ViewGroup, page: ReaderPage): View =
        when (page) {
            is ReaderPage.ChapterPage -> createPageView(container, PageType.PAGE_VIEW)
            is ReaderPage.ChapterTransition -> createPageView(container, PageType.TRANSITION_VIEW)
        }

    fun createPageView(container: ViewGroup, type: PageType): View

    fun destroyPageView(container: ViewGroup, position: Int, view: View)

    fun setPages(pages: List<ReaderPage>)

}
package com.discut.manga.ui.reader.viewer

import com.discut.manga.ui.reader.domain.ReaderPage

interface PagesContainer {


    var isVisible: Boolean

    fun onScrolled(index: Int? = null)

    fun moveToPage(position: Int)

    fun destroy()

    fun setPages(pages: List<ReaderPage>)

}
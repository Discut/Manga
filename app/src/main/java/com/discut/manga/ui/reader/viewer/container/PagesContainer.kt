package com.discut.manga.ui.reader.viewer.container

import com.discut.manga.ui.reader.viewer.domain.ReaderPage

interface PagesContainer {


    var isVisible: Boolean

    fun onScrolled(index: Int? = null)

    fun moveToPage(position: Int)

    fun destroy()

    fun setPages(pages: List<ReaderPage>)

}
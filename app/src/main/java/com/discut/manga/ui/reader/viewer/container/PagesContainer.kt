package com.discut.manga.ui.reader.viewer.container

interface PagesContainer {


    var isVisible: Boolean

    fun onScrolled(index: Int? = null)

    fun moveToPage(position: Int)

}
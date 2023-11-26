package com.discut.manga.ui.reader.viewer.domain

class Chapter(
    pageLoader: PageLoader
) : PageLoader by pageLoader {

    private val readerPages = mutableListOf<ReaderPage>()

    fun getPage(index: Int): ReaderPage {
        return readerPages[index]
    }

}
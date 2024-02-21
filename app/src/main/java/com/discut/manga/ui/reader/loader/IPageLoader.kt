package com.discut.manga.ui.reader.loader

import com.discut.manga.ui.reader.domain.ReaderPage

interface IPageLoader {


    suspend fun buildPages(): List<ReaderPage>


    /*    suspend fun loadPage(page: Page) {
            if (page.isLoad.not()) {
                _loadPage(page)
            }
        }*/

    suspend fun loadPage(readerPage: ReaderPage)


    fun destroy() {

    }
}
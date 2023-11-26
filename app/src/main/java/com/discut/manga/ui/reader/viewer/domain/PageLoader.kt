package com.discut.manga.ui.reader.viewer.domain

interface PageLoader {


    suspend fun getAllPages(): List<ReaderPage>


    /*    suspend fun loadPage(page: Page) {
            if (page.isLoad.not()) {
                _loadPage(page)
            }
        }*/

    suspend fun loadPage(readerPage: ReaderPage)


    fun destroy() {

    }
}
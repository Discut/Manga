package com.discut.manga.ui.reader.loader

import com.discut.manga.ui.reader.domain.ReaderPage
import java.io.File

class LocalPageLoader(val file: File) : IPageLoader {
    override suspend fun buildPages(): List<ReaderPage> {
        TODO("Not yet implemented")
    }

    override suspend fun loadPage(readerPage: ReaderPage) {
        TODO("Not yet implemented")
    }
}
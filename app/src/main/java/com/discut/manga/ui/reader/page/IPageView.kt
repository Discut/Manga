package com.discut.manga.ui.reader.page

import com.discut.manga.ui.reader.domain.ReaderPage

interface IPageView {
    fun bind(readerPage: ReaderPage)
}
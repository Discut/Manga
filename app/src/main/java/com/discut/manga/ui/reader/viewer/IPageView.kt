package com.discut.manga.ui.reader.viewer

import com.discut.manga.ui.reader.viewer.domain.ReaderPage

interface IPageView {
    fun bind(readerPage: ReaderPage)
}
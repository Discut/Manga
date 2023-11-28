package com.discut.manga.ui.reader.domain

import com.discut.manga.ui.reader.viewer.domain.ReaderChapter

data class CurrentChapters(
    val prevReaderChapter: ReaderChapter?,
    val currReaderChapter: ReaderChapter,
    val nextReaderChapter: ReaderChapter?
)
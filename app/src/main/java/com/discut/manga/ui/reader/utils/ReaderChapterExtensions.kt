package com.discut.manga.ui.reader.utils

import com.discut.manga.ui.reader.viewer.domain.ReaderChapter

fun List<ReaderChapter>.getPrevOrNull(index: Int): ReaderChapter? =
    if (isAscending)
        getOrNull(index - 1)
    else getOrNull(index + 1)

fun List<ReaderChapter>.getNextOrNull(index: Int): ReaderChapter? =
    if (isAscending)
        getOrNull(index + 1)
    else getOrNull(index - 1)

val List<ReaderChapter>.isAscending: Boolean
    get() {
        if (this.size < 2) return true
        return this[0].dbChapter.chapterNumber < this[1].dbChapter.chapterNumber
    }
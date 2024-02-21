package com.discut.manga.ui.reader.utils

import com.discut.manga.ui.reader.domain.ReaderPage

fun List<ReaderPage>.getIndexOfRealFirst(): Int =
    indexOfFirst { it is ReaderPage.ChapterTransition } + 1

fun List<ReaderPage>.getIndexOfRealLast(): Int {
    indexOfLast { it is ReaderPage.ChapterTransition }.apply {
        if (this != -1) return this
        return size - 1
    }
}

fun List<ReaderPage>.getRealSize(): Int =
    getIndexOfRealLast() - getIndexOfRealFirst() + 1

fun List<ReaderPage>.getRealPosition(position:Int): Int =
    position - getIndexOfRealFirst() + 1

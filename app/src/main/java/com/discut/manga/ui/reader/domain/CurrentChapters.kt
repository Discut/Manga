package com.discut.manga.ui.reader.domain

import com.discut.manga.ui.reader.viewer.domain.ReaderChapter
import com.discut.manga.ui.reader.viewer.domain.ReaderPage

data class CurrentChapters(
    val prevReaderChapter: ReaderChapter?,
    val currReaderChapter: ReaderChapter,
    val nextReaderChapter: ReaderChapter?
) {
    val pages: List<ReaderPage>
        get() {
            val prevPages = if (prevReaderChapter?.state is ReaderChapter.State.Loaded) {
                (prevReaderChapter.state as ReaderChapter.State.Loaded).pages.takeLast(2)
            } else emptyList()
            val nextPages = if (nextReaderChapter?.state is ReaderChapter.State.Loaded) {
                (nextReaderChapter.state as ReaderChapter.State.Loaded).pages.take(2)
            } else emptyList()
            return prevPages +
                    listOf(ReaderPage.ChapterTransition(prevReaderChapter, currReaderChapter)) +
                    (currReaderChapter.state as ReaderChapter.State.Loaded).pages +
                    listOf(ReaderPage.ChapterTransition(currReaderChapter, nextReaderChapter)) +
                    nextPages
        }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (javaClass != other.javaClass) return false

        other as CurrentChapters

        if (prevReaderChapter != other.prevReaderChapter) return false
        if (currReaderChapter != other.currReaderChapter) return false
        if (nextReaderChapter != other.nextReaderChapter) return false

        prevReaderChapter?.let {
            if (it.state != other.prevReaderChapter?.state) return false
        }

        if (currReaderChapter.state != other.currReaderChapter.state) return false

        nextReaderChapter?.let {
            if (it.state != other.nextReaderChapter?.state) return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = prevReaderChapter?.hashCode() ?: 0
        result = 31 * result + currReaderChapter.hashCode()
        result = 31 * result + (nextReaderChapter?.hashCode() ?: 0)
        return result
    }
}
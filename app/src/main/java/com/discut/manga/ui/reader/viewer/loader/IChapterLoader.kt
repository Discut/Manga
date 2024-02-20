package com.discut.manga.ui.reader.viewer.loader

import com.discut.manga.ui.reader.viewer.domain.ReaderChapter

interface IChapterLoader {

    /**
     * Notice: This method just load info of chapter's pages, would not load stream of any pages.
     */
    suspend fun loadChapter(chapter: ReaderChapter)
}
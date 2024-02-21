package com.discut.manga.ui.reader.loader

import com.discut.manga.ui.reader.domain.ReaderChapter

interface IChapterLoader {

    /**
     * Notice: This method just load info of chapter's pages, would not load stream of any pages.
     */
    suspend fun loadChapter(chapter: ReaderChapter)
}
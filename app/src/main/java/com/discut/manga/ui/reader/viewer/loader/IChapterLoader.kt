package com.discut.manga.ui.reader.viewer.loader

import com.discut.manga.ui.reader.viewer.domain.ReaderChapter

interface IChapterLoader {
    suspend fun loadChapter(chapter: ReaderChapter)
}
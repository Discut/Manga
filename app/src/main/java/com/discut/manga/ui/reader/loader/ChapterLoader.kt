package com.discut.manga.ui.reader.loader

import android.content.Context
import com.discut.manga.data.toSChapter
import com.discut.manga.ui.reader.domain.ReaderChapter
import com.discut.manga.util.withIOContext
import discut.manga.data.chapter.Chapter
import discut.manga.data.manga.Manga
import discut.manga.source.local.LocalSource
import discut.manga.source.local.SupportFormat
import manga.source.HttpSource
import manga.source.Source

class ChapterLoader(
    private val context: Context,
    private val manga: Manga,
    private val source: Source,
) : IChapterLoader {

    override suspend fun loadChapter(chapter: ReaderChapter) {
        if (isLoaded(chapter)) {
            return
        }
        chapter.state = ReaderChapter.State.Loading
        val pageLoader = buildPageLoader(chapter.dbChapter)
        chapter.state = withIOContext {
            val buildPages = pageLoader.buildPages()
            if (buildPages.isEmpty()) {
                return@withIOContext ReaderChapter.State.Error(Exception("No pages found."))
            }
            return@withIOContext ReaderChapter.State.Loaded(buildPages)
        }
    }

    private fun buildPageLoader(chapter: Chapter): IPageLoader {
        return when (source) {
            is LocalSource -> {
                when (val format = source.getFormat(chapter.toSChapter())) {
                    is SupportFormat.Directory -> DirectoryPageLoader(format.file, context)
                }
                // TODO 添加更多的格式支持
            }
            is HttpSource -> HttpPageLoader(chapter, source)
            else -> {
                throw Exception("Unsupported")
            }
        }
    }

    private fun isLoaded(chapter: ReaderChapter): Boolean {
        return when (chapter.state) {
            is ReaderChapter.State.Loaded -> true
            else -> false
        }
    }

}
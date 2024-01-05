package com.discut.manga.ui.reader.viewer.loader

import android.content.Context
import com.discut.manga.ui.reader.viewer.domain.PageState
import com.discut.manga.ui.reader.viewer.domain.ReaderPage
import discut.manga.source.local.disk.LocalSourceFileSystem
import manga.core.utils.ImageUtil
import manga.core.utils.compareToCaseInsensitiveNaturalOrder
import java.io.File
import java.io.FileInputStream

class DirectoryPageLoader(private val file: File, private val context: Context) : IPageLoader {
    override suspend fun buildPages(): List<ReaderPage> {
        return file.listFiles()
            ?.filter { !it.isDirectory && ImageUtil.isImage(it.name) { FileInputStream(file) } }
            ?.sortedWith { left, right -> left.name.compareToCaseInsensitiveNaturalOrder(right.name) }
            ?.mapIndexed { index, file ->
                val streamFn = { FileInputStream(file) }
                ReaderPage.ChapterPage(index).apply {
                    streamGetter = streamFn
                    state = PageState.READY
                    loadPage = {}
                    loadUrl = {}
                }
            }
            .orEmpty()
            .toList()
    }

    override suspend fun loadPage(readerPage: ReaderPage) {
        TODO("Not yet implemented")
    }
}
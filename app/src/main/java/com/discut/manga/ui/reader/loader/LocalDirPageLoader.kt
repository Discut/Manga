package com.discut.manga.ui.reader.loader

import com.discut.manga.ui.reader.viewer.domain.PageState
import com.discut.manga.ui.reader.viewer.domain.ReaderPage
import manga.core.utils.ImageUtil
import manga.core.utils.compareToCaseInsensitiveNaturalOrder
import java.io.File
import java.io.FileInputStream

class LocalDirPageLoader(private val file: File) : PageLoader() {
    override var isLocal: Boolean = true

    override suspend fun getPages(): List<ReaderPage> {
        return File(file.absolutePath).listFiles().orEmpty()
            .filter { !it.isDirectory && ImageUtil.isImage(it.name) { FileInputStream(it) } }
            .sortedWith { left, right -> left.name.compareToCaseInsensitiveNaturalOrder(right.name) }
            .mapIndexed { i, file ->
                val streamFn = { FileInputStream(file) }
                ReaderPage.ChapterPage(i).apply {
                    streamGetter = streamFn
                    state = PageState.READY
                }
            }.orEmpty()
    }

}
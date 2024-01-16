package com.discut.manga.service.saver.download

import com.discut.manga.service.saver.download.model.Downloader
import manga.source.domain.Page
import manga.core.utils.ImageUtil
import java.io.File
import java.io.InputStream

class DownloadFileSystem(
    private val baseDir: String
) {
    private fun getChapterDir(downloader: Downloader): String {
        return "$baseDir/${downloader.manga.id}/${downloader.chapter.id}"
    }

    fun savePage(downloader: Downloader, page: Page, pageInputStream: InputStream) {
        val chapterDir = getChapterDir(downloader)
        val tempPageFile = File("$chapterDir/${page.index}.temp")
        tempPageFile.parentFile?.mkdirs()
        tempPageFile.writeBytes(pageInputStream.readBytes()).let {
            pageInputStream.close()
        }
        if (ImageUtil.isImage("") { tempPageFile.inputStream() }.not()) {
            tempPageFile.delete()
            page.status = Page.State.ERROR
            return
        }
        tempPageFile.renameTo(
            File(
                "$chapterDir/${page.number}.${
                    ImageUtil.findImageType(
                        tempPageFile.inputStream()
                    )!!.extension
                }"
            )
        )
        page.status = Page.State.READY
    }

    fun deleteChapter(downloader: Downloader): Boolean {
        val chapterDir = getChapterDir(downloader)
        if (File(chapterDir).exists()) {
            return File(chapterDir).deleteRecursively()
        }
        return false
    }
}
package discut.manga.source.local.manager

import android.content.Context
import discut.manga.source.local.SupportFormat
import discut.manga.source.local.disk.LocalSourceFileSystem
import managa.source.domain.SChapter
import managa.source.domain.SManga
import managa.source.domain.utils.ChapterRecognition
import manga.core.utils.compareToCaseInsensitiveNaturalOrder
import java.io.File

class LocalMangaManager(
    private val context: Context,
    private val fileSystem: LocalSourceFileSystem
) {
    fun getAllManga(): Sequence<SManga> {
        val baseDirsFiles = fileSystem.getFilesInBaseDirectories()

        val maybeMangaDirs = baseDirsFiles
            // 留下未隐藏文件夹
            .filter { it.isDirectory && !it.isHidden }
            // 根据名称排名
            .distinctBy { it.name }
        /*            // 留下内容大于0的文件夹
                    .filter { it.listFiles()?.size?.let { size -> size > 0 } ?: false }*/

        return maybeMangaDirs.map {
            SManga.create().apply {
                title = it.name
                url = it.absolutePath
            }
        }
    }

    fun getChaptersOfManga(manga: SManga): List<SChapter> {
        return fileSystem.getFilesInMangaDir(manga.url)
            // 留下app支持格式的文件或者文件夹
            .filter { SupportFormat.isSupport(it) }
            .map {
                SChapter.create().apply {
                    name = if (it.isDirectory) {
                        it.name
                    } else {
                        it.nameWithoutExtension
                    }
                    url = it.absolutePath
                    date_upload = it.lastModified()
                    chapter_number = ChapterRecognition
                        .parseChapterNumber(manga.title, this.name, this.chapter_number.toDouble())
                        .toFloat()
                }
            }.sortedWith { left, right ->
                return@sortedWith right.chapter_number.compareTo(left.chapter_number)
                    .let {
                        if (it == 0) right.name.compareToCaseInsensitiveNaturalOrder(left.name) else it
                    }
            }
            .toList()
    }

    fun getFormat(chapter: SChapter): SupportFormat {
        val file = File(chapter.url)
        if (file.exists()) {
            throw Exception("No found such file.")
        }
        return SupportFormat.valueOf(file)
    }

}
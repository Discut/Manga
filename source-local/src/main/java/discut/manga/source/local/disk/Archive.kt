package discut.manga.source.local.disk

import java.io.File

object Archive {

    private val SUPPORTED_ARCHIVE_TYPES = listOf("zip", "cbz"/*, "rar", "cbr", "epub"*/)

    fun isSupported(file: File): Boolean = with(file) {
        if (isDirectory) {
            return true
        }
        return extension.lowercase() in SUPPORTED_ARCHIVE_TYPES
    }
}

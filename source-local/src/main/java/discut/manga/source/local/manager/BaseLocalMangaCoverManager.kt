package discut.manga.source.local.manager

import manga.source.domain.SManga
import java.io.File
import java.io.InputStream

interface BaseLocalMangaCoverManager {
    /**
     * ## update cover of manga.
     */
    fun update(manga: SManga, inputStream: InputStream): File?
}
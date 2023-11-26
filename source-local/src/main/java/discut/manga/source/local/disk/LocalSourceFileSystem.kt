package discut.manga.source.local.disk

import android.content.Context
import android.content.res.Configuration
import discut.manga.common.res.R
import manga.core.disk.DiskUtils
import java.io.File
import java.util.Locale

class LocalSourceFileSystem(
    private val context: Context
) {
    private val baseLocationFolder =
        "${getString(context, R.string.app_name, Locale.ENGLISH)}${File.separator}local"

    fun getBaseDirectories(): Sequence<File> {
        return DiskUtils.getExternalStorages(context)
            .map { File(it.absolutePath, baseLocationFolder) }
            .asSequence()
    }

    fun getFilesInBaseDirectories(): Sequence<File> {
        return getBaseDirectories()
            // Get all the files inside all baseDir
            .flatMap { it.listFiles().orEmpty().toList() }
    }

    fun getFilesInMangaDir(path: String): Sequence<File> {
        return File(path).listFiles().orEmpty().asSequence()
    }
}

private fun getString(context: Context, id: Int, locale: Locale): String {
    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(locale)
    return context.createConfigurationContext(configuration).resources.getString(id)
}
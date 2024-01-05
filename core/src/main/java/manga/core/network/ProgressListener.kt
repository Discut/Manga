package manga.core.network

/**
 * from tachiyomi
 */
interface ProgressListener {
    fun update(bytesRead: Long, contentLength: Long, done: Boolean)
}

package discut.manga.source.local

import java.io.File

sealed interface SupportFormat {
    data class Directory(val file: File) : SupportFormat
    /*    data class Zip(val file: File) : SupportFormat
        data class Rar(val file: File) : SupportFormat
        data class Epub(val file: File) : SupportFormat*/

    class UnknownFormatException : Exception()


    companion object {
        fun valueOf(file: File) = with(file) {
            when {
                isDirectory -> Directory(this)
                /*                extension.equals("zip", true) || extension.equals("cbz", true) -> Zip(this)
                                extension.equals("rar", true) || extension.equals("cbr", true) -> Rar(this)
                                extension.equals("epub", true) -> Epub(this)*/
                else -> throw UnknownFormatException()
            }
        }

        fun isSupport(file: File): Boolean {
            try {
                valueOf(file)
            } catch (e: Exception) {
                return false
            }
            return true
        }
    }
}
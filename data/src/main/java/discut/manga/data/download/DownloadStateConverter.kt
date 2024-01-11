package discut.manga.data.download

import androidx.room.TypeConverter

class DownloadStateConverter {

    @TypeConverter
    fun objectToInt(state: DownloadState): Int {
        return state.code
    }

    @TypeConverter
    fun intToState(code: Int): DownloadState {
        return DownloadState.entries.first { it.code == code }
    }

    @TypeConverter
    fun objectToString(arr: List<Long>): String {
        val builder = StringBuilder()
        arr.forEach {
            builder.append(it).append(",")
        }
        return builder.toString()
    }

    @TypeConverter
    fun stringToObject(str: String): List<Long> {
        return str.split(",").mapNotNull {
            try {
                it.toLong()
            } catch (e: Exception) {
                null
            }
        }
    }
}
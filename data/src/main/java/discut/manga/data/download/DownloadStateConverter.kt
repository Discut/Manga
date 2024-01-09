package discut.manga.data.download

import androidx.room.TypeConverter

class DownloadStateConverter {

    @TypeConverter
    fun objectToInt(state: DownloadState): Int {
        return state.code
    }

    @TypeConverter
    fun intToState(code: Int): DownloadState {
        return DownloadState.values().first { it.code == code }
    }
}
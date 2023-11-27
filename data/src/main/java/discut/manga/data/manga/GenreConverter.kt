package discut.manga.data.manga

import androidx.room.TypeConverter

class GenreConverter {

    @TypeConverter
    fun objectToSting(list: List<String>?): String {
        if (list == null) {
            return ""
        }
        var str = ""
        list.forEach {
            str += "$it,"
        }
        return str
    }

    @TypeConverter
    fun stringToObject(str: String):List<String>{
        val split = str.split(",")
        return split.toList()
    }
}
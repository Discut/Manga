package discut.manga.data

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface BaseDao<T> {
    @Delete
    fun delete(manga: T): Int

    @Update
    fun update(manga: T): Int

    @Insert
    fun insert(manga: T)

    @Insert
    fun insertAll(vararg manga: T)
}
package discut.manga.data

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface BaseDao<T> {
    @Delete
    fun delete(entity: T): Int

    @Update
    fun update(entity: T): Int

    @Insert
    fun insert(entity: T)

    @Insert
    fun insertAll(vararg entities: T)
}
package discut.manga.data.category

import androidx.room.Dao
import androidx.room.Query
import discut.manga.data.BaseDao
import discut.manga.data.chapter.Chapter
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao : BaseDao<Category> {
    @Query(
        "SELECT * FROM category"
    )
    fun getAll(): List<Chapter>

    @Query(
        "SELECT * FROM category"
    )
    fun getAllFlow(): Flow<List<Category>>

    @Query(
        "SELECT * FROM category WHERE id = :id"
    )
    fun getById(id: Long): Category?

}
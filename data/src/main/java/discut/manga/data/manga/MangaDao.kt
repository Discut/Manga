package discut.manga.data.manga

import androidx.room.Dao
import androidx.room.Query
import discut.manga.data.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaDao : BaseDao<Manga> {
    @Query(
        "SELECT * FROM manga"
    )
    fun getAll(): List<Manga>

    @Query(
        "SELECT * FROM manga WHERE id = :id"
    )
    fun getById(id: Long): Manga

    @Query(
        "SELECT * FROM manga WHERE id = :id"
    )
    fun getByIdAsFlow(id: Long): Flow<Manga>
}
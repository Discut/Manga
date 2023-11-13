package discut.manga.data.manga

import androidx.room.Dao
import androidx.room.Query
import discut.manga.data.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaEntityDao : BaseDao<MangaEntity> {
    @Query(
        "SELECT * FROM manga"
    )
    fun getAll(): List<MangaEntity>

    @Query(
        "SELECT * FROM manga WHERE id = :id"
    )
    fun getById(id: Long): MangaEntity

    @Query(
        "SELECT * FROM manga WHERE id = :id"
    )
    fun getByIdAsFlow(id: Long): Flow<MangaEntity>
}
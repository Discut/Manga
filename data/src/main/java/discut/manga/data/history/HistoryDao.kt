package discut.manga.data.history

import androidx.room.Dao
import androidx.room.Query
import discut.manga.data.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao : BaseDao<History> {

    @Query(
        "SELECT * FROM history"
    )
    fun getAll(): List<History>

    @Query(
        "SELECT * FROM history"
    )
    fun getAllAsFlow(): Flow<List<History>>

    @Query(
        "SELECT * FROM history WHERE mangaId = :mangaId"
    )
    fun getByMangaId(mangaId: Long): History?
}
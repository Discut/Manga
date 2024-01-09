package discut.manga.data.download

import androidx.room.Dao
import androidx.room.Query
import discut.manga.data.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao : BaseDao<Download> {
    @Query(
        "SELECT * FROM download"
    )
    fun getAll(): List<Download>

    @Query(
        "SELECT * FROM download"
    )
    fun getAllAsFlow(): Flow<List<Download>>

    @Query(
        "SELECT * FROM download WHERE mangaId = :mangaId"
    )
    fun getByMangaId(mangaId: Long): Download?

}
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
        "SELECT * FROM download WHERE id = :id"
    )
    fun getById(id: Long): Download?

    @Query(
        "SELECT * FROM download WHERE mangaId = :mangaId"
    )
    fun getAllByMangaId(mangaId: Long): List<Download>

    @Query(
        "SELECT * FROM download"
    )
    fun getAllAsFlow(): Flow<List<Download>>

    @Query(
        "SELECT * FROM download WHERE mangaId = :mangaId AND chapterId = :chapterId"
    )
    fun getByMangaIdAndChapterId(mangaId: Long, chapterId: Long): Download?

    @Query(
        "SELECT * FROM download WHERE mangaId = :mangaId"
    )
    fun getByMangaIdAndChapterId(mangaId: Long): List<Download>

    @Query(
        "SELECT * FROM download WHERE mangaId = :mangaId AND chapterId = :chapterId"
    )
    fun getByMangaIdAndChapterIdAsFlow(mangaId: Long, chapterId: Long): Flow<Download?>

    @Query(
        "UPDATE download SET `order` = :order WHERE id = :id"
    )
    fun updateOrder(id: Long, order: Double)
}
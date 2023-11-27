package discut.manga.data.chapter

import androidx.room.Dao
import androidx.room.Query
import discut.manga.data.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao : BaseDao<Chapter> {
    @Query(
        "SELECT * FROM chapter"
    )
    fun getAll(): List<Chapter>

    @Query(
        "SELECT * FROM chapter WHERE mangaId = :mangaId"
    )
    fun getAllInManga(mangaId: Long): List<Chapter>

    @Query(
        "SELECT * FROM chapter WHERE id = :id"
    )
    fun getById(id: Long): Chapter

    @Query(
        "SELECT * FROM chapter WHERE id = :id"
    )
    fun getByIdAsFlow(id: Long): Flow<Chapter>

    @Query(
        "SELECT * FROM chapter WHERE url = :url AND mangaId = :mangaId"
    )
    fun getByUrlAndMangaId(url: String, mangaId: Long): Chapter?
}
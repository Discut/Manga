package discut.manga.data.source

import androidx.room.Dao
import androidx.room.Query
import discut.manga.data.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface SourceRepoDao : BaseDao<SourceRepo> {

    @Query(
        "SELECT * FROM source_repo"
    )
    fun getAll(): List<SourceRepo>

    @Query(
        "SELECT * FROM source_repo"
    )
    fun getAllAsFlow(): Flow<List<SourceRepo>>
}
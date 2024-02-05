package discut.manga.data.source

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "source_repo"
)
data class SourceRepo(
    @PrimaryKey
    val id: Long,
    val name: String,
    val url: String,
    val order: Float
) {
    companion object {
        fun create() = SourceRepo(
            id = -1,
            name = "",
            url = "",
            order = 0f
        )
    }
}

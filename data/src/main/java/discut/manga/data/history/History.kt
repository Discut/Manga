package discut.manga.data.history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "history"
)
data class History(
    @PrimaryKey
    val id: Long,
    val mangaId: Long,
    val chapterId: Long,
    val readAt: Long,
) {
    companion object {
        fun create() = History(
            id = -1,
            mangaId = -1,
            chapterId = -1,
            readAt = 0
        )
    }
}

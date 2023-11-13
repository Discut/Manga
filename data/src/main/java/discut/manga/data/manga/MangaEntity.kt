package discut.manga.data.manga

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "manga"
)
data class MangaEntity(
    @PrimaryKey
    val id: Long,
    val source: Long,
    val favorite: Boolean,
    val lastUpdate: Long,
    val nextUpdate: Long,
    val fetchInterval: Int,
    val dateAdded: Long,
/*    val viewerFlags: Long,
    val chapterFlags: Long,*/
    val coverLastModified: Long,
    val url: String,
    val title: String,
    val artist: String?,
    val author: String?,
    val description: String?,
    val genre: String?,//such as "Action,Adventure"
    val status: Long,
    val thumbnailUrl: String?,
    val initialized: Boolean,
    val lastModifiedAt: Long,
    val favoriteModifiedAt: Long?,
)
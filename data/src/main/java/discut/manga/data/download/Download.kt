package discut.manga.data.download

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(
    tableName = "download"
)
@TypeConverters(DownloadStateConverter::class)
data class Download(
    @PrimaryKey
    val id: Long,
    val mangaId: Long,
    val chapterId: Long,
    val order: Double,
    val status: DownloadState,
    val queue: List<Long>,
    val downloaded: List<Long>,
    val addAt: Long,
)

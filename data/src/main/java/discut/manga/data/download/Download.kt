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
    val status: DownloadState,
    val currentPage: Int,
    val totalPage: Int,
    val addAt: Long,
)

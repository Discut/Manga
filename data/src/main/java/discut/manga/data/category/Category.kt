package discut.manga.data.category

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "category",
)
data class Category(
    @PrimaryKey
    val id: Long,
    val name: String,
    val order: Long,
    /*val flags: Long,*/
) : Serializable {

    @Ignore
    val isDefaultCategory: Boolean = id == UNCATEGORIZED_ID

    companion object {
        const val UNCATEGORIZED_ID = 0L
    }
}

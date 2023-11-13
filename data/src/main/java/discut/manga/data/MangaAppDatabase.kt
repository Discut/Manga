package discut.manga.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import discut.manga.data.manga.MangaEntity
import discut.manga.data.manga.MangaEntityDao

@Database(
    entities = [MangaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MangaAppDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaEntityDao

    companion object {
        private var INSTANCE: MangaAppDatabase? = null
        private const val DATABASE_NAME = "manga.db"

        val DB
            get() = getDb()

        fun init(context: Context) {
            if (INSTANCE == null) {
                synchronized(MangaAppDatabase::class.java) {
                    if (INSTANCE == null) {
                        initDb(context)
                    }
                }
            }
        }

        private fun initDb(context: Context) {
            INSTANCE = Room.databaseBuilder(
                context,
                MangaAppDatabase::class.java, DATABASE_NAME
            ).build()
        }

        private fun getDb(): MangaAppDatabase {
            return INSTANCE ?: throw IllegalStateException("AppDatabase is not initialized")
        }
    }
}
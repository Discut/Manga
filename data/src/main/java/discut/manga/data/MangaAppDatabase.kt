package discut.manga.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import discut.manga.data.chapter.Chapter
import discut.manga.data.chapter.ChapterDao
import discut.manga.data.manga.Manga
import discut.manga.data.manga.MangaDao

@Database(
    entities = [Manga::class, Chapter::class],
    version = 1,
    exportSchema = false
)
abstract class MangaAppDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao

    abstract fun chapterDao(): ChapterDao

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
            )
                //.fallbackToDestructiveMigration()
                .build()
        }

        private fun getDb(): MangaAppDatabase {
            return INSTANCE ?: throw IllegalStateException("AppDatabase is not initialized")
        }
    }
}
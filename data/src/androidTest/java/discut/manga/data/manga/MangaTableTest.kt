package discut.manga.data.manga

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import discut.manga.data.MangaAppDatabase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MangaTableTest {

    @Before
    fun before(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        MangaAppDatabase.init(appContext)
    }

    @Test
    fun testMangaQuery(){
        val mangaDao = MangaAppDatabase.DB.mangaDao()
        val byId = mangaDao.getById(112)
        byId.toString()
    }
}
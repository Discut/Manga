package discut.manga.source.local

import android.content.Context
import discut.manga.source.local.disk.LocalSourceFileSystem
import discut.manga.source.local.manager.LocalMangaManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import managa.source.Source
import managa.source.domain.FilterList
import managa.source.domain.SChapter
import managa.source.domain.SManga
import managa.source.domain.SMangas

class LocalSource(
    private val context: Context,
) : Source {

    private var localMangaManager: LocalMangaManager

    init {
        localMangaManager = LocalMangaManager(
            context,
            LocalSourceFileSystem(context)
        )
    }
    override val id: Long
        get() = 0L
    override val name: String
        get() = "LocalSource"
    override val language: String
        get() = "All"

    override fun fetchChapterList(manga: SManga): Flow<List<SChapter>> {
        return flow {
            val chaptersOfManga = localMangaManager.getChaptersOfManga(manga)
            emit(chaptersOfManga)
        }
    }

    override suspend fun getPopularManga(page: Int): SMangas =
        getSearchManga(page, "", FilterList())

    override suspend fun getSearchManga(page: Int, query: String, filters: FilterList): SMangas {
        val mangas = localMangaManager.getAllManga()
            // 留下包含查询字符串的文件夹
            .filter { it.title.contains(query, ignoreCase = true) }


        // TODO 暂无用处
        mangas.forEach {
            val chapters = localMangaManager.getChaptersOfManga(it)
            if (chapters.isEmpty()) {
                return@forEach
            }

        }

        // do somethings

        return SMangas(mangas = mangas.toList(), hasNextPage = false)
    }

    override suspend fun getLatestUpdates(page: Int): SMangas =
        getSearchManga(page, "", FilterList())


    override fun getFilterList(): FilterList = FilterList()
}
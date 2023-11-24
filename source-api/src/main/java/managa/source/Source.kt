package managa.source

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import managa.source.domain.FilterList
import managa.source.domain.Page
import managa.source.domain.SChapter
import managa.source.domain.SManga
import managa.source.domain.SMangas
import okhttp3.internal.wait
import kotlin.coroutines.coroutineContext

/**
 * Its a basic interface from [Source], extension developer can extend it.
 * it could be online source or local source.
 */
interface Source {
    val id: Long
    val name: String
    val language: String

    fun fetchMangaDetails(manga: SManga): Flow<SManga> =
        throw IllegalStateException("Not used")

    fun fetchChapterList(manga: SManga): Flow<List<SChapter>> =
        throw IllegalStateException("Not used")

    fun fetchPageList(chapter: SChapter): Flow<List<Page>> =
        throw IllegalStateException("Not used")

    /**
     * Get a page with a list of manga.
     *
     * @since extensions-lib 1.5
     * @param page the page number to retrieve.
     */
    @Suppress("DEPRECATION")
    suspend fun getPopularManga(page: Int): Flow<SMangas> {
        return fetchPopularManga(page)
    }

    /**
     * Get a page with a list of manga.
     *
     * @since extensions-lib 1.5
     * @param page the page number to retrieve.
     * @param query the search query.
     * @param filters the list of filters to apply.
     */
    @Suppress("DEPRECATION")
    suspend fun getSearchManga(page: Int, query: String, filters: FilterList): MangasPage {
        return fetchSearchManga(page, query, filters).awaitSingle()
    }

    /**
     * Get a page with a list of latest manga updates.
     *
     * @since extensions-lib 1.5
     * @param page the page number to retrieve.
     */
    @Suppress("DEPRECATION")
    fun getLatestUpdates(page: Int): SMangas {
        CoroutineScope(Dispatchers.Default).launch {
            fetchLatestUpdates(page)
                .collect{

                }
        }
        fetchLatestUpdates(page)
            .launchIn().join()
    }

    /**
     * Returns the list of filters for the source.
     */
    fun getFilterList(): FilterList

    @Deprecated(
        "Source object, please use get_method",
        ReplaceWith("getPopularManga"),
    )
    suspend fun fetchPopularManga(page: Int): Flow<SMangas> =
        throw IllegalStateException("Not used")

    @Deprecated(
        "Use the non-RxJava API instead",
        ReplaceWith("getSearchManga"),
    )
    fun fetchSearchManga(page: Int, query: String, filters: FilterList): Flow<SMangas> =
        throw IllegalStateException("Not used")

    @Deprecated(
        "Use the non-RxJava API instead",
        ReplaceWith("getLatestUpdates"),
    )
    fun fetchLatestUpdates(page: Int): Flow<SMangas> =
        throw IllegalStateException("Not used")
}
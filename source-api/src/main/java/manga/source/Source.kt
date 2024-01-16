package manga.source

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import manga.source.domain.FilterList
import manga.source.domain.Page
import manga.source.domain.SChapter
import manga.source.domain.SManga
import manga.source.domain.SMangas

interface BaseSource {
    val id: Long
    val name: String
    val language: String
}

/**
 * Its a basic interface from [Source], extension developer can extend it.
 * it could be online source or local source.
 */
interface Source : BaseSource {

    fun fetchMangaDetails(manga: SManga): Flow<SManga> =
        throw IllegalStateException("Not used")

    fun fetchChapterList(manga: SManga): Flow<List<SChapter>> =
        throw IllegalStateException("Not used")

    fun fetchPageList(chapter: SChapter): Flow<List<Page>> =
        throw IllegalStateException("Not used")


    /**
     * Get info about a manga.
     *
     *
     */
    suspend fun getMangaDetails(manga: SManga): SManga {
        return fetchMangaDetails(manga).first()
    }

    /**
     * Get a list of chapters.
     */
    suspend fun getChapterList(manga: SManga): List<SChapter> {
        return fetchChapterList(manga).first()
    }

    /**
     * Get a list of pages.
     */
    suspend fun getPageList(chapter: SChapter): List<Page> {
        return fetchPageList(chapter).first()
    }
    /**
     * Get a page with a list of manga.
     *
     * @since extensions-lib 1.5
     * @param page the page number to retrieve.
     */
    @Suppress("DEPRECATION")
    suspend fun getPopularManga(page: Int): SMangas {
        return fetchPopularManga(page).first()
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
    suspend fun getSearchManga(page: Int, query: String, filters: FilterList): SMangas {
        return fetchSearchManga(page, query, filters).first()
    }

    /**
     * Get a page with a list of latest manga updates.
     *
     * @since extensions-lib 1.5
     * @param page the page number to retrieve.
     */
    @Suppress("DEPRECATION")
    suspend fun getLatestUpdates(page: Int): SMangas {
        return fetchLatestUpdates(page)
            .first()
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
    suspend fun fetchSearchManga(page: Int, query: String, filters: FilterList): Flow<SMangas> =
        throw IllegalStateException("Not used")

    @Deprecated(
        "Use the non-RxJava API instead",
        ReplaceWith("getLatestUpdates"),
    )
    fun fetchLatestUpdates(page: Int): Flow<SMangas> =
        throw IllegalStateException("Not used")
}
package managa.source

import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import managa.source.domain.FilterList
import managa.source.domain.Page
import managa.source.domain.SChapter
import managa.source.domain.SManga
import managa.source.domain.SMangas
import manga.core.application.application
import manga.core.network.GET
import manga.core.network.NetworkHelper
import manga.core.network.asFlow
import okhttp3.Headers
import okhttp3.Request
import okhttp3.Response
import java.net.URI
import java.net.URISyntaxException

abstract class HttpSource : Source, ConfigurationSource {

    val networkHelper = NetworkHelper(application!!.cacheDir)

    val headers: Headers by lazy {
        headersBuilder().build()
    }
    abstract val baseUrl: String

    open val client
        get() = networkHelper.client

    companion object {
        const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/114.0"
    }

    @Deprecated(
        "Source object, please use get_method",
        replaceWith = ReplaceWith("getPopularManga")
    )
    override suspend fun fetchPopularManga(page: Int): Flow<SMangas> = callbackFlow {
        client.newCall(popularMangaRequest(page))
            .asFlow()
            .catch {
                close(it)
            }.collect {
                val popularMangaParse = popularMangaParse(it)
                trySendBlocking(popularMangaParse).onFailure { e ->
                    close(e)
                }
            }
        awaitClose {
            cancel()
        }
    }

    protected abstract fun popularMangaRequest(page: Int): Request
    protected abstract fun popularMangaParse(response: Response): SMangas

    override suspend fun fetchSearchManga(
        page: Int,
        query: String,
        filters: FilterList,
    ): Flow<SMangas> = callbackFlow {
        client.newCall(searchMangaRequest(page, query, filters))
            .asFlow()
            .catch {
                close(it)
            }.collect {
                val searchMangaParse = searchMangaParse(it)
                trySendBlocking(searchMangaParse).onFailure { e ->
                    close(e)
                }
            }
        awaitClose {
            cancel()
        }
    }

    protected abstract fun searchMangaRequest(
        page: Int,
        query: String,
        filters: FilterList,
    ): Request

    protected abstract fun searchMangaParse(response: Response): SMangas

    override fun fetchMangaDetails(manga: SManga): Flow<SManga> = callbackFlow {
        client.newCall(mangaDetailsRequest(manga))
            .asFlow()
            .catch {
                close(it)
            }.collect {
                val mangaDetailsParse = mangaDetailsParse(it)
                trySendBlocking(mangaDetailsParse).onFailure { e ->
                    close(e)
                }
            }
        awaitClose {
            cancel()
        }
    }

    /**
     * Returns the request for the details of a manga. Override only if it's needed to change the
     * url, send different headers or request method like POST.
     *
     * @param manga the manga to be updated.
     */
    open fun mangaDetailsRequest(manga: SManga): Request {
        return GET(baseUrl + manga.url, headers)
    }

    /**
     * Parses the response from the site and returns the details of a manga.
     *
     * @param response the response from the site.
     */
    protected abstract fun mangaDetailsParse(response: Response): SManga


    override fun fetchChapterList(manga: SManga): Flow<List<SChapter>> = callbackFlow {
        client.newCall(chapterListRequest(manga))
            .asFlow()
            .catch {
                close(it)
            }.collect {
                val chapterListParse = chapterListParse(it)
                trySendBlocking(chapterListParse).onFailure { e ->
                    close(e)
                }
            }
        awaitClose {
            cancel()
        }
    }

    /**
     * Returns the request for updating the chapter list. Override only if it's needed to override
     * the url, send different headers or request method like POST.
     *
     * @param manga the manga to look for chapters.
     */
    protected open fun chapterListRequest(manga: SManga): Request {
        return GET(baseUrl + manga.url, headers)
    }

    /**
     * Parses the response from the site and returns a list of chapters.
     *
     * @param response the response from the site.
     */
    protected abstract fun chapterListParse(response: Response): List<SChapter>


    override fun fetchPageList(chapter: SChapter): Flow<List<Page>> = callbackFlow {
        client.newCall(pageListRequest(chapter))
            .asFlow()
            .catch {
                close(it)
            }.collect {
                val pageListParse = pageListParse(it)
                trySendBlocking(pageListParse).onFailure { e ->
                    close(e)
                }
            }
        awaitClose {
            cancel()
        }
    }

    /**
     * Returns the request for getting the page list. Override only if it's needed to override the
     * url, send different headers or request method like POST.
     *
     * @param chapter the chapter whose page list has to be fetched.
     */
    protected open fun pageListRequest(chapter: SChapter): Request {
        return GET(baseUrl + chapter.url, headers)
    }

    /**
     * Parses the response from the site and returns a list of pages.
     *
     * @param response the response from the site.
     */
    protected abstract fun pageListParse(response: Response): List<Page>


    open fun fetchImageUrl(page: Page): Flow<String> = callbackFlow {
        client.newCall(imageUrlRequest(page))
            .asFlow()
            .catch {
                close(it)
            }.collect {
                val imageUrlParse = imageUrlParse(it)
                trySendBlocking(imageUrlParse).onFailure { e ->
                    close(e)
                }
            }
        awaitClose {
            cancel()
        }
    }

    /**
     * Returns the request for getting the url to the source image. Override only if it's needed to
     * override the url, send different headers or request method like POST.
     *
     * @param page the chapter whose page list has to be fetched
     */
    protected open fun imageUrlRequest(page: Page): Request {
        return GET(page.url, headers)
    }

    /**
     * Parses the response from the site and returns the absolute url to the source image.
     *
     * @param response the response from the site.
     */
    protected abstract fun imageUrlParse(response: Response): String

    /**
     * Assigns the url of the chapter without the scheme and domain. It saves some redundancy from
     * database and the urls could still work after a domain change.
     *
     * @param url the full url to the chapter.
     */
    fun SChapter.setUrlWithoutDomain(url: String) {
        this.url = getUrlWithoutDomain(url)
    }

    /**
     * Returns the url of the given string without the scheme and domain.
     *
     * @param orig the full url.
     */
    private fun getUrlWithoutDomain(orig: String): String {
        return try {
            val uri = URI(orig.replace(" ", "%20"))
            var out = uri.path
            if (uri.query != null) {
                out += "?" + uri.query
            }
            if (uri.fragment != null) {
                out += "#" + uri.fragment
            }
            out
        } catch (e: URISyntaxException) {
            orig
        }
    }


    protected open fun headersBuilder() = Headers.Builder().apply {
        add(
            "User-Agent",
            USER_AGENT
        )
    }

}
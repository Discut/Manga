package manga.core.network

import android.webkit.CookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

// from eu.kanade.tachiyomi.network.AndroidCookieJar
// https://github.com/tachiyomiorg/tachiyomi/blob/45d8411f98d831cd8c734089ea20a6ef06ef948f/core/src/main/java/eu/kanade/tachiyomi/network/AndroidCookieJar.kt#L8
class AndroidCookieJar : CookieJar {
    private val manager = CookieManager.getInstance()
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return getCookies(url)
    }

    private fun getCookies(url: HttpUrl): List<Cookie> {
        val cookies = manager.getCookie(url.toString())
        return if (cookies != null && cookies.isNotEmpty()) {
            cookies.split(";").mapNotNull { Cookie.parse(url, it) }
        } else {
            emptyList()
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookies.forEach {
            manager.setCookie(url.toString(), it.toString())
        }
    }

    fun remove(url: HttpUrl, cookieNames: List<String>? = null, maxAge: Int = -1): Int {
        val urlString = url.toString()
        val cookies = manager.getCookie(urlString) ?: return 0

        fun List<String>.filterNames(): List<String> {
            return if (cookieNames != null) {
                this.filter { it in cookieNames }
            } else {
                this
            }
        }

        return cookies.split(";")
            .map { it.substringBefore("=") }
            .filterNames()
            .onEach { manager.setCookie(urlString, "$it=;Max-Age=$maxAge") }
            .count()
    }

    fun removeAll() {
        manager.removeAllCookies {}
    }
}
package manga.core.network

import android.content.Context
import manga.core.network.interceptor.UncaughtExceptionInterceptor
import manga.core.network.interceptor.UserAgentInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

class NetworkHelper(
    private val cacheDir: File
    /*private val preferences: NetworkPreferences,*/
) {
    companion object {
        const val MAX_CACHE_SIZE = 10L * 1024 * 1024 // 10 MiB
    }

    val enableVerboseLogging = false // preferences.verboseLogging().get()

    val cookieJar = AndroidCookieJar()

    val client: OkHttpClient = run {
        val builder = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .callTimeout(2, TimeUnit.MINUTES)
            .cache(
                Cache(
                    directory = File(cacheDir, "network_cache"),
                    maxSize = MAX_CACHE_SIZE, // 10 MiB
                ),
            )
            .addInterceptor(UncaughtExceptionInterceptor())
            .addInterceptor(UserAgentInterceptor { "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/114.0" })

        if (enableVerboseLogging) {
            val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            }
            builder.addNetworkInterceptor(httpLoggingInterceptor)
        }

        /*        builder.addInterceptor(
                    CloudflareInterceptor(context, cookieJar, ::defaultUserAgentProvider),
                )*/

        /*
                when (preferences.dohProvider().get()) {
                    PREF_DOH_CLOUDFLARE -> builder.dohCloudflare()
                    PREF_DOH_GOOGLE -> builder.dohGoogle()
                    PREF_DOH_ADGUARD -> builder.dohAdGuard()
                    PREF_DOH_QUAD9 -> builder.dohQuad9()
                    PREF_DOH_ALIDNS -> builder.dohAliDNS()
                    PREF_DOH_DNSPOD -> builder.dohDNSPod()
                    PREF_DOH_360 -> builder.doh360()
                    PREF_DOH_QUAD101 -> builder.dohQuad101()
                    PREF_DOH_MULLVAD -> builder.dohMullvad()
                    PREF_DOH_CONTROLD -> builder.dohControlD()
                    PREF_DOH_NJALLA -> builder.dohNajalla()
                    PREF_DOH_SHECAN -> builder.dohShecan()
                }
        */

        builder.build()
    }

    /**
     * @deprecated Since extension-lib 1.5
     */
    @Deprecated("The regular client handles Cloudflare by default")
    @Suppress("UNUSED")
    val cloudflareClient: OkHttpClient = client

}

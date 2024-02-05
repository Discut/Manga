package com.discut.manga.service.source

import com.discut.manga.data.source.Extension
import com.discut.manga.util.withIOContext
import kotlinx.serialization.json.Json
import manga.core.application.application
import manga.core.network.GET
import manga.core.network.HttpException
import manga.core.network.NetworkHelper
import manga.core.network.ProgressListener
import manga.core.network.ProgressResponseBody
import manga.source.extensions.toInputStream
import okhttp3.Request
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteExtensionsGetter @Inject constructor() {

    private val networkHelper by lazy {
        NetworkHelper(application!!.cacheDir)
    }

    /*    suspend fun fetchAllSourcesIn(urls: List<String>): List<Extension.RemoteExtension> {
            val sources = mutableListOf<Extension.RemoteExtension>()
            urls.forEach {
                sources += try {
                    fetchSourcesIn(it)
                } catch (e: Exception) {
                    emptyList()
                }
            }
            return sources
        }*/

    suspend fun fetchExtensionsIn(url: String): Result<List<Extension.RemoteExtension>> {
        return try {
            val get = Request.Builder()
                .url(url)
                .get()
                .build()
            val response = withIOContext {
                networkHelper.client.newCall(get).execute()
            }
            if (response.isSuccessful.not()) {
                throw Exception("response is not successful")
            }
            Result.success(parsingSourceList(response.body.string()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchExtensionApk(url: String, progressListener: ProgressListener? = null): InputStream {
        if (url.isBlank()) {
            throw IllegalArgumentException("url is blank")
        }
        val call = networkHelper.client
            .newBuilder()
            .cache(null)
            .apply {
                if (progressListener != null) {
                    addNetworkInterceptor {
                        val progressInterceptor = it.proceed(it.request())
                        progressInterceptor.newBuilder()
                            .body(ProgressResponseBody(progressInterceptor.body, progressListener))
                            .build()
                    }
                }
            }
            .build()
            .newCall(GET(url))
        val response = withIOContext {
            call.execute()
        }
        if (!response.isSuccessful) {
            response.close()
            throw HttpException(response.code)
        }
        return response.toInputStream()
    }

    private fun parsingSourceList(body: String): List<Extension.RemoteExtension> {
        return Json.decodeFromString<List<Extension.RemoteExtension>>(body)
    }
}
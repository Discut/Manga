package com.discut.manga.service.cache

import android.content.Context
import com.discut.manga.App
import com.discut.manga.service.GlobalModuleEntrypoint
import com.jakewharton.disklrucache.DiskLruCache
import dagger.hilt.EntryPoints
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.use
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PagesCache @Inject constructor(
    @ApplicationContext context: Context
) {
    private val cache: DiskLruCache
    fun get(key: String): String? {
        return cache.get(key)?.getInputStream(DISK_IMAGE_CACHE_INDEX)
            ?.use { it.readBytes().decodeToString() }
    }

    fun isExist(key: String): Boolean {
        return cache.get(key) != null
    }

    fun put(key: String, value: String) {
        cache.edit(key)?.apply {
            try {
                newOutputStream(DISK_IMAGE_CACHE_INDEX).use {
                    it.write(value.toByteArray())
                }
                commit()
            } catch (e: Exception) {
                abort()
            } finally {
                cache.flush()
            }
        }
    }

    init {
        val diskCacheDir = File(context.cacheDir, DISK_IMAGE_CACHE_NAME)//context.cacheDir
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs()
        }
        cache = DiskLruCache.open(
            diskCacheDir,
            DISK_IMAGE_CACHE_VERSION,
            1,
            DISK_IMAGE_CACHE_SIZE
        )
    }

    companion object {
        private const val DISK_IMAGE_CACHE_SIZE = 1024L * 1024 * 50 //50MB
        private const val DISK_IMAGE_CACHE_VERSION = 1
        private const val DISK_IMAGE_CACHE_NAME = "pages_cache"
        private const val DISK_IMAGE_CACHE_INDEX = 0
    }
}

val PagesCache.Companion.instance: PagesCache
    get() = EntryPoints.get(App.instance, GlobalModuleEntrypoint::class.java).getPagesCacheInstance()
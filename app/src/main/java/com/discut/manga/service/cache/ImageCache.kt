package com.discut.manga.service.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.discut.manga.App
import com.discut.manga.service.GlobalModuleEntrypoint
import com.jakewharton.disklrucache.DiskLruCache
import dagger.hilt.EntryPoints
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ImageCache @Inject constructor(
    @ApplicationContext context: Context
) {

    private val cache: DiskLruCache

    fun get(key: String): Bitmap? {
        return (cache.get(key)?.getInputStream(DISK_IMAGE_CACHE_INDEX) as? FileInputStream)?.use {
            val fileDescriptor = it.fd
            val decodeSampledBitmapFromFileDescriptor =
                decodeSampledBitmapFromFileDescriptor(fileDescriptor)
            decodeSampledBitmapFromFileDescriptor
        }
    }

    fun getAsStream(key: String): InputStream {
        return cache.get(key)?.getInputStream(DISK_IMAGE_CACHE_INDEX)
            ?: throw Exception("key not found")
    }

    fun isExist(key: String): Boolean {
        return cache.get(key) != null
    }

    fun put(key: String, value: InputStream) {
        cache.edit(key)?.apply {
            try {
                newOutputStream(DISK_IMAGE_CACHE_INDEX).use { it.write(value.readBytes()) }
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

    private fun decodeSampledBitmapFromFileDescriptor(
        fileDescriptor: FileDescriptor?,
        scale: Double = 1.0
    ): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        // 获取图片的原始宽高信息
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
        val reqWidth = (options.outWidth * scale).toInt()
        val reqHeight = (options.outHeight * scale).toInt()
        return decodeSampledBitmapFromFileDescriptor(fileDescriptor, reqWidth, reqHeight)
    }

    private fun decodeSampledBitmapFromFileDescriptor(
        fileDescriptor: FileDescriptor?,
        reqWidth: Int,
        reqHeight: Int,
    ): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        // 获取图片的原始宽高信息
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)

        // 计算采样率
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // 根据计算出的采样率进行实际解码
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    companion object {
        private const val DISK_IMAGE_CACHE_SIZE = 1024L * 1024 * 1024 //1024MB
        private const val DISK_IMAGE_CACHE_VERSION = 1
        private const val DISK_IMAGE_CACHE_NAME = "image_cache"
        private const val DISK_IMAGE_CACHE_INDEX = 0
    }
}

val ImageCache.Companion.instance: ImageCache
    get() = EntryPoints.get(App.instance, GlobalModuleEntrypoint::class.java).getImageCacheInstance()
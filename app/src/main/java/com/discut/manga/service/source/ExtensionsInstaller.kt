package com.discut.manga.service.source

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.discut.manga.data.source.Extension
import java.io.File
import java.io.InputStream


class ExtensionsInstaller(
    val extension: Extension.RemoteExtension
) {

    companion object {
        const val APK_MIME = "application/vnd.android.package-archive"
        const val EXTRA_EXTENSION_PKG = "ExtensionInstaller.extra.EXTENSION_PKG"

    }

    fun install(path: String, context: Context): Result<Unit> {
        //val path1 = "/storage/emulated/0/BH3.apk"
        return try {
            val contentUri = FileProvider
                .getUriForFile(context, "${context.packageName}.fileprovider", File(path))
            Intent(context, ExtensionInstallerActivity::class.java)
                .apply {
                    setDataAndType(contentUri, APK_MIME)
                    putExtra(EXTRA_EXTENSION_PKG, extension.pkg)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                .also {
                    context.startActivity(it)
                }
            /*Intent(Intent.ACTION_VIEW)
                .apply {
                    setDataAndType(
                        contentUri,
                        *//*Uri.fromFile(File(path)),*//*
                        "application/vnd.android.package-archive"
                    )
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                .also {
                    context.startActivity(it)
                }*/
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun install(apkInputStream: InputStream, context: Context): Result<Unit> {
        val apkTmpId = System.currentTimeMillis()
        val apkFile = File(context.cacheDir.absoluteFile, "$apkTmpId.apk")
        return try {
            apkFile.parentFile?.mkdirs()
            apkFile.writeBytes(apkInputStream.readBytes()).let {
                apkInputStream.close()
            }
            install(apkFile.canonicalPath, context)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            /*if (apkFile.exists()) {
                apkFile.delete()
            }*/
        }
    }
}



package com.discut.manga.service.source

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.pm.PackageInfoCompat
import com.discut.manga.data.source.Extension
import dalvik.system.PathClassLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import manga.source.Source
import manga.source.SourceFactory

object ExtensionsLoader {

    private const val EXTENSION_FEATURE = "manga.extension"
    private const val METADATA_SOURCE_CLASS = "manga.extension.class"
    private const val METADATA_SOURCE_FACTORY = "manga.extension.factory"


    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Suppress("DEPRECATION")
    private val PACKAGE_FLAGS = PackageManager.GET_CONFIGURATIONS or
            PackageManager.GET_META_DATA or
            PackageManager.GET_SIGNATURES or
            PackageManager.GET_SIGNING_CERTIFICATES

    fun loadExtensions(context: Context): List<Extension.LocalExtension> {
        val packageManager = context.packageManager

        val installedExtensions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(PACKAGE_FLAGS.toLong()))
        } else {
            packageManager.getInstalledPackages(PACKAGE_FLAGS)
        }.filter { isExtension(it) }

        return runBlocking(context = SupervisorJob() + Dispatchers.IO) {
            val queue = installedExtensions
                .distinctBy { it.packageName }
                .map {
                    async { loadExtension(context, it) }
                }
            queue.awaitAll()
        }
    }

    private fun loadExtension(context: Context, extensionInfo: PackageInfo): Extension.LocalExtension {
        val pkgManager = context.packageManager
        val appInfo = extensionInfo.applicationInfo
        val pkgName = extensionInfo.packageName

        val extName =
            pkgManager.getApplicationLabel(appInfo).toString().substringAfter("Manga:")
        val versionName = extensionInfo.versionName
        val versionCode = PackageInfoCompat.getLongVersionCode(extensionInfo)

        if (versionName.isNullOrEmpty()) {
            return Extension.LocalExtension.Error(
                name = extName,
                pkg = pkgName,
                version = versionName,
                versionCode = versionCode,
                pkgFactory = null,
                sources = emptyList(),
                icon = null,
                error = Exception("Extension $extName has no version name"),
                msg = "Extension $extName has no version name",
            )
        }

        val classLoader = PathClassLoader(appInfo.sourceDir, null, context.classLoader)
        val sources = appInfo.metaData.getString(METADATA_SOURCE_CLASS)!!
            .split(";")
            .map {
                val sourceClass = it.trim()
                if (sourceClass.startsWith(".")) {
                    extensionInfo.packageName + sourceClass
                } else {
                    sourceClass
                }
            }
            .flatMap {
                try {
                    when (val obj = Class.forName(it, false, classLoader).getDeclaredConstructor()
                        .newInstance()) {
                        is Source -> listOf(obj)
                        is SourceFactory -> obj.createSources()
                        else -> throw Exception("Unknown source class type! ${obj.javaClass}")
                    }
                } catch (e: Throwable) {
                    return Extension.LocalExtension.Error(
                        name = extName,
                        pkg = pkgName,
                        version = versionName,
                        versionCode = versionCode,
                        pkgFactory = null,
                        sources = emptyList(),
                        icon = null,
                        error = e,
                        msg = "Error loading extension $extName",
                    )
                }
            }

        return Extension.LocalExtension.Success(
            name = extName,
            pkg = pkgName,
            version = versionName,
            versionCode = versionCode,
            pkgFactory = appInfo.metaData.getString(METADATA_SOURCE_FACTORY),
            sources = sources,
            icon = appInfo.loadIcon(pkgManager),
        )
    }

    private fun isExtension(pkgInfo: PackageInfo): Boolean {
        return pkgInfo.reqFeatures.orEmpty().any { it.name == EXTENSION_FEATURE }
    }
}
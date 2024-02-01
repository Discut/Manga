package com.discut.manga.service.source

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.discut.manga.App
import com.discut.manga.data.source.Extension
import com.discut.manga.service.GlobalModuleEntrypoint
import com.discut.manga.util.launchIO
import com.discut.manga.util.withIOContext
import dagger.hilt.EntryPoints
import dagger.hilt.android.qualifiers.ApplicationContext
import discut.manga.data.MangaAppDatabase
import discut.manga.source.local.LocalSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import manga.core.base.BaseManager
import manga.source.Source
import manga.source.online.Baimangu
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SourceManager @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val remoteExtensionsGetter: RemoteExtensionsGetter,
) : ISourceManager, BaseManager {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val sourcesRepoDao by lazy {
        MangaAppDatabase.DB.sourceRepoDao()
    }

    private lateinit var localSources: Map<Long, Source>

    private val _sourcesMapFlow = MutableStateFlow(ConcurrentHashMap<Long, Source>())
    override fun initManager() {
        //val concurrentHashMap = ConcurrentHashMap<Long, Source>()
        val localSource = LocalSource(context)
        val baimangu = Baimangu()
        //val bh3 = BH3()
        //concurrentHashMap[localSource.id] = localSource
        //concurrentHashMap[baimangu.id] = baimangu
        localSources = mapOf(
            localSource.id to localSource,
            baimangu.id to baimangu
        )
        //concurrentHashMap[bh3.id] = bh3
        _sourcesMapFlow.value = ConcurrentHashMap(localSources)

        subscribeInstalledExtensions()
        updateInstalledExtensions()
    }

    private fun updateInstalledExtensions() {
        val localExtensions = _installedExtensionsFlow.value
        val extensionList = ExtensionsLoader.loadExtensions(context)
        val pkgs = localExtensions.associate { it.pkg to it.versionCode }
        val newExtensions =
            extensionList.filter { pkgs.getOrDefault(it.pkg, -1L) != it.versionCode }
        val newPkgs = newExtensions.map { it.pkg }
        val oldExtensions = localExtensions.filter { it.pkg !in newPkgs }
        val installed =
            _installedExtensionsFlow.value.filter { it.pkg !in extensionList.map { it.pkg } }
                .toSet()
        _installedExtensionsFlow.update {
            oldExtensions + newExtensions - installed
        }
        _allExtensionsFlow.update { all ->
            (all.filter { old -> newExtensions.none { new -> old.pkg == new.pkg } } + newExtensions).sortedBy { it.name } - installed
        }
        /*val extensions = ExtensionsLoader.loadExtensions(context)

        val uninstalled =
            _installedExtensionsFlow.value.filter { it.pkg !in extensions.map { it.pkg } }
        val newInstalled =
            extensions.filter { it.pkg !in _installedExtensionsFlow.value.map { it.pkg } }
        _installedExtensionsFlow.update { list ->
            list - uninstalled.toSet() + newInstalled
        }
        val filter = extensions.filter { it.pkg !in _allExtensionsFlow.value.map { it.pkg } }
        _allExtensionsFlow.update { extensionsBuffer ->
            (extensionsBuffer + filter)
                .filter { (it is Extension.LocalExtension && it.pkg in uninstalled.map { it.pkg }).not() }
        }*/
    }

    private fun subscribeInstalledExtensions() {
        scope.launchIO {
            installedExtensionsFlow
                .collect { extensions ->
                    val sources = extensions.filterIsInstance<Extension.LocalExtension.Success>()
                        .flatMap { it.sources }
                    _sourcesMapFlow.update { sourceMap ->
                        ConcurrentHashMap(localSources + sources.associateBy { it.id })
                    }
                }
        }
    }

    private val _installedExtensionsFlow: MutableStateFlow<List<Extension.LocalExtension>> =
        MutableStateFlow(emptyList())
    private val _allExtensionsFlow: MutableStateFlow<List<Extension>> =
        MutableStateFlow(emptyList())


    override val installedExtensionsFlow: StateFlow<List<Extension.LocalExtension>>
        get() = _installedExtensionsFlow.asStateFlow()
    override val allExtensionsFlow: StateFlow<List<Extension>>
        get() = _allExtensionsFlow.asStateFlow()

    override fun get(sourceKey: Long): Source? {
        return _sourcesMapFlow.value[sourceKey]
    }

    override suspend fun updateAllExtensionsList() {
        withIOContext {
            updateInstalledExtensions()
            val sourceRepos = sourcesRepoDao.getAll()
            if (sourceRepos.isEmpty()) {
                return@withIOContext
            }
            val urls = sourceRepos
                .sortedBy { it.order }
                .map { it.url }
            val queue = urls.map {
                async {
                    val result = remoteExtensionsGetter.fetchExtensionsIn(it)
                    if (result.isFailure) {
                        Log.e(TAG, "[${it}] fetchSourcesIn failed: ${result.exceptionOrNull()}")
                        return@async
                    }
                    val remoteExtensions = result.getOrNull() ?: return@async
                    val filterRemoteExtensions = remoteExtensions
                        .filter { remoteExtension ->
                            _installedExtensionsFlow.value.none { it.pkg == remoteExtension.pkg }
                        }
                        .filter { remoteExtension ->
                            _allExtensionsFlow.value.none { it.pkg == remoteExtension.pkg }
                        }
                    _allExtensionsFlow.update { extensions ->
                        (extensions + filterRemoteExtensions).sortedBy { it.name }
                    }
                }
            }
            queue.awaitAll()
        }
    }

    override fun getAll(): List<Source> {
        return _sourcesMapFlow.value.values.toList()
    }

    override fun getAllAsFlow(): Flow<List<Source>> = _sourcesMapFlow.map {
        it.elements().toList()
    }

    override fun install(extension: Extension.RemoteExtension) {
        scope.launchIO {
            try {
                val stream =
                    remoteExtensionsGetter.fetchExtensionApk(extension.apkUrl, extension)
                extension.onInstall()
                val result = ExtensionsInstaller(extension).install(stream, context)
                if (result.isFailure) {
                    Log.e(TAG, "install: ", result.exceptionOrNull())
                }
            } catch (e: Exception) {
                Log.e(TAG, "install: ", e)
            }
        }
    }

    override fun uninstall(extension: Extension.LocalExtension.Success) {
        try {
            val uri = Uri.fromParts("package", extension.pkg, null)
            Intent(Intent.ACTION_DELETE, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(this)
            }
        } catch (e: Exception) {
            Log.e(TAG, "uninstall: ", e)
        }
    }

    companion object {
        private const val TAG = "SourceManager"

        val instance by lazy {
            EntryPoints.get(App.instance, GlobalModuleEntrypoint::class.java)
                .getSourceManagerInstance()
        }

    }
}
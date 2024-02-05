package com.discut.manga.service.source

import com.discut.manga.data.source.Extension
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import manga.source.Source

interface ISourceManager {

    val installedExtensionsFlow: StateFlow<List<Extension.LocalExtension>>
    val allExtensionsFlow: StateFlow<List<Extension>>
    fun get(sourceKey: Long): Source?

    suspend fun updateAllExtensionsList()

    fun getAll(): List<Source>

    fun getAllAsFlow(): Flow<List<Source>>

    fun install(extension: Extension.RemoteExtension)

    fun uninstall(extension: Extension.LocalExtension.Success)
}
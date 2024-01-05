package com.discut.manga.service.source

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import discut.manga.source.local.LocalSource
import kotlinx.coroutines.flow.MutableStateFlow
import managa.source.Source
import managa.source.online.BH3
import managa.source.online.Baimangu
import manga.core.base.BaseManager
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SourceManager @Inject constructor(@ApplicationContext private val context: Context) :
    ISourceManager, BaseManager {

    private val sourcesMapFlow = MutableStateFlow(ConcurrentHashMap<Long, Source>())
    override fun initManager() {
        val concurrentHashMap = ConcurrentHashMap<Long, Source>()
        val localSource = LocalSource(context)
        val baimangu = Baimangu()
        val bh3 = BH3()
        concurrentHashMap[localSource.id] = localSource
        concurrentHashMap[baimangu.id] = baimangu
        concurrentHashMap[bh3.id] = bh3
        sourcesMapFlow.value = concurrentHashMap
    }

    override fun get(sourceKey: Long): Source? {
        return sourcesMapFlow.value[sourceKey]
    }

    override fun getAll(): List<Source> {
        return sourcesMapFlow.value.values.toList()
    }
}
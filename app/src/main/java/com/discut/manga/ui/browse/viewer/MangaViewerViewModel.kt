package com.discut.manga.ui.browse.viewer

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.discut.core.mvi.BaseViewModel
import com.discut.manga.service.manga.IMangaProvider
import com.discut.manga.service.manga.NetworkMangaSaver
import com.discut.manga.service.source.ISourceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import discut.manga.data.manga.Manga
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import manga.source.domain.SManga
import javax.inject.Inject

@HiltViewModel
class MangaViewerViewModel
@Inject constructor(
    private val sourceManager: ISourceManager,
    private val mangaProvider: IMangaProvider,
    private val networkMangaSaver: NetworkMangaSaver
) : BaseViewModel<MangaViewerState, MangaViewerEvent, MangaViewerEffect>() {
    override fun initialState(): MangaViewerState = MangaViewerState()

    override suspend fun handleEvent(
        event: MangaViewerEvent,
        state: MangaViewerState
    ): MangaViewerState? {
        when (event) {
            is MangaViewerEvent.Init -> {
                return try {
                    val source = sourceManager.get(event.sourceId)
                    assert(source != null) {
                        "Source not found"
                    }
                    state.copy(
                        isLoading = false,
                        status = MangaViewerStatus.Success(
                            createMangasFlow(event.sourceId)
                        )
                    )
                } catch (e: Throwable) {
                    state.copy(
                        isLoading = false,
                        status = MangaViewerStatus.Error(e)
                    )
                }
            }

        }
    }

    private suspend fun createMangasFlow(sourceId: Long): Flow<PagingData<StateFlow<Manga>>> {
        val info = uiState.value.queryMangasInfo
        return createPager(info, sourceId).flow
            .map {
                it.toManga(sourceId)
            }.cachedIn(CoroutineScope(Dispatchers.IO))
    }

    // 定义一个函数来创建分页对象
    private fun createPager(info: QueryMangasInfo, sourceId: Long): Pager<Long, SManga> {
        return Pager(PagingConfig(pageSize = PAGE_SIZE)) {
            when (info) {
                is QueryMangasInfo.Popular -> {
                    mangaProvider.getPopular(sourceId)
                }

                is QueryMangasInfo.Latest -> {
                    mangaProvider.getLatest(sourceId)
                }

                is QueryMangasInfo.Search -> {
                    mangaProvider.search(sourceId, info.query, info.filterList)
                }
            }
        }
    }

    companion object {
        const val PAGE_SIZE = 12
    }

    private suspend fun PagingData<SManga>.toManga(sourceId: Long): PagingData<StateFlow<Manga>> =
        map { sm ->
            networkMangaSaver.trySave(sm, sourceId).let { m ->
                mangaProvider.subscribe(m.id)
            }.filterNotNull()
                .stateIn(scope = CoroutineScope(Dispatchers.IO))
        }


}
package com.discut.manga.ui.browse.viewer

import androidx.paging.PagingData
import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import discut.manga.data.manga.Manga
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import manga.source.domain.FilterList

data class MangaViewerState(
    val isLoading: Boolean = true,
    val queryMangasInfo: QueryMangasInfo = QueryMangasInfo.Popular,
    val status: MangaViewerStatus = MangaViewerStatus.Waiting
) : UiState

sealed interface MangaViewerEvent : UiEvent {
    data class Init(val sourceId: Long, val queryKey: String) : MangaViewerEvent
}

sealed interface MangaViewerEffect : UiEffect {
    data object Loading : MangaViewerEffect
}

sealed interface MangaViewerStatus {
    data object Waiting : MangaViewerStatus
    data class Error(val error: Throwable) : MangaViewerStatus
    data class Success(val mangasFlow: Flow<PagingData<StateFlow<Manga>>>) : MangaViewerStatus
}

sealed class QueryMangasInfo(val queryKey: String?, val filterList: FilterList) {
    data object Popular : QueryMangasInfo(null, FilterList())

    data object Latest : QueryMangasInfo(null, FilterList())

    data class Search(val query: String) : QueryMangasInfo(query, FilterList())
}
package com.discut.manga.ui.history

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import com.discut.manga.domain.history.MangaChapterHistory
import com.discut.manga.ui.history.component.HistoryItemType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class HistoryState(
    internal val queryKeyFlow: MutableStateFlow<String> = MutableStateFlow(""),
    internal val histories: StateFlow<List<HistoryAction>> = MutableStateFlow(listOf()),
    internal val historyListLayout: HistoryItemType = HistoryItemType.LOOSE
) : UiState

sealed interface HistoryEvent : UiEvent {
    data object Init : HistoryEvent

    data class Remove(val history: MangaChapterHistory) : HistoryEvent

    data object ClearAll : HistoryEvent

    data class Search(val query: String) : HistoryEvent

    data class ChangeListLayout(val layout: HistoryItemType) : HistoryEvent

    data class ChangedListLayout(val layout: HistoryItemType) : HistoryEvent
}

sealed interface HistoryEffect : UiEffect

sealed class HistoryAction(
    val key: String
) {
    data class Header(val title: String) : HistoryAction(title)
    data class Item(val history: MangaChapterHistory) : HistoryAction(history.historyId.toString())
}
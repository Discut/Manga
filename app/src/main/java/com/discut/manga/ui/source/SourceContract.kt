package com.discut.manga.ui.source

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import com.discut.manga.ui.source.tab.TabContent
import com.discut.manga.ui.source.tab.Tabs
import managa.source.Source

data class SourceScreenState(
    val isLoading: Boolean = false,
    val currentTab: Int = 0,
    val tabs: List<TabContent> = Tabs,
    val sourceItems: List<SourceItem> = listOf(),
) : UiState

sealed interface SourceScreenEvent : UiEvent {
    object Init : SourceScreenEvent
}

sealed interface SourceScreenEffect : UiEffect {
    object Loading : SourceScreenEffect
}

sealed class SourceItem(val label: String) {
    abstract val sources: List<Source>

    data class Default(override val sources: List<Source>) : SourceItem("Default")
    data class Custom(override val sources: List<Source>) : SourceItem("Custom")
}
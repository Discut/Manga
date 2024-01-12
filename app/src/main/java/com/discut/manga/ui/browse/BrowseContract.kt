package com.discut.manga.ui.browse

import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import com.discut.manga.ui.browse.tab.TabContent
import com.discut.manga.ui.browse.tab.Tabs
import manga.source.Source

data class BrowseScreenState(
    val isLoading: Boolean = false,
    val currentTab: Int = 0,
    val tabs: List<TabContent> = Tabs,
    val sourceItems: List<SourceItem> = listOf(),
) : UiState

sealed interface BrowseScreenEvent : UiEvent {
    object Init : BrowseScreenEvent
}

sealed interface BrowseScreenEffect : UiEffect {
    object Loading : BrowseScreenEffect
}

sealed class SourceItem(val label: String) {
    abstract val sources: List<Source>

    data object Nothing : SourceItem("Nothing") {
        override val sources: List<Source>
            get() = listOf()
    }

    data class Default(override val sources: List<Source>) : SourceItem("Default")
    data class Custom(override val sources: List<Source>) : SourceItem("Custom")
}
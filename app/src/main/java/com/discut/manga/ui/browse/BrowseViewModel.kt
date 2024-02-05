package com.discut.manga.ui.browse

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.service.source.ISourceManager
import com.discut.manga.service.source.isLocal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import manga.source.Source
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val sourceManager: ISourceManager
) :
    BaseViewModel<BrowseScreenState, BrowseScreenEvent, BrowseScreenEffect>() {
    override fun initialState(): BrowseScreenState = BrowseScreenState(
        sourceItemsState = MutableStateFlow(
            buildSourceItemList(sourceManager.getAll())
        )
    )

    init {
        sendEvent(BrowseScreenEvent.Init)
    }

    override suspend fun handleEvent(
        event: BrowseScreenEvent,
        state: BrowseScreenState
    ): BrowseScreenState {
        return when (event) {
            is BrowseScreenEvent.Init -> {
                state.copy(
                    sourceItemsState = sourceManager.getAllAsFlow()
                        .map(::buildSourceItemList)
                        .stateIn(CoroutineScope(Dispatchers.IO))
                )
            }

            else -> state
        }
    }

    private fun buildSourceItemList(sources: List<Source>): List<SourceItem> {
        val sourceItems = mutableListOf<SourceItem>()
        val defaults = mutableListOf<Source>()
        val custom = mutableListOf<Source>()
        sources.forEach {
            if (it.isLocal()) {
                defaults.add(it)
            }
        }
        custom.addAll(
            sources.filter { it.isLocal().not() }
        )
        sourceItems.add(SourceItem.Default(defaults))
        sourceItems.add(SourceItem.Custom(custom))
        return sourceItems
    }

}
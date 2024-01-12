package com.discut.manga.ui.browse

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.service.source.ISourceManager
import com.discut.manga.service.source.isLocal
import dagger.hilt.android.lifecycle.HiltViewModel
import manga.source.Source
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val sourceManager: ISourceManager
) :
    BaseViewModel<BrowseScreenState, BrowseScreenEvent, BrowseScreenEffect>() {
    override fun initialState(): BrowseScreenState = BrowseScreenState()

    init {
        val sourceItems = mutableListOf<SourceItem>()
        val all = sourceManager.getAll()
        val defaults = mutableListOf<Source>()
        val custom = mutableListOf<Source>()
        all.forEach {
            if (it.isLocal()) {
                defaults.add(it)
            }
        }
        custom.addAll(
            all.filter { it.isLocal().not() }
        )
        sourceItems.add(SourceItem.Default(defaults))
        sourceItems.add(SourceItem.Custom(custom))
        sendState {
            copy(
                sourceItems = sourceItems
            )
        }
    }

    override suspend fun handleEvent(
        event: BrowseScreenEvent,
        state: BrowseScreenState
    ): BrowseScreenState {
        return when (event) {
            is BrowseScreenEvent.Init -> {

                state
            }

            else -> state
        }
    }

}
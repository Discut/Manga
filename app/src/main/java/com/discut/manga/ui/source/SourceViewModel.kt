package com.discut.manga.ui.source

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.source.ISourceManager
import com.discut.manga.source.isLocal
import dagger.hilt.android.lifecycle.HiltViewModel
import managa.source.Source
import javax.inject.Inject

@HiltViewModel
class SourceViewModel @Inject constructor(
    private val sourceManager: ISourceManager
) :
    BaseViewModel<SourceScreenState, SourceScreenEvent, SourceScreenEffect>() {
    override fun initialState(): SourceScreenState = SourceScreenState()

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
        event: SourceScreenEvent,
        state: SourceScreenState
    ): SourceScreenState {
        return when (event) {
            is SourceScreenEvent.Init -> {

                state
            }

            else -> state
        }
    }

}
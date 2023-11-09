package com.discut.manga.ui.reader

import com.discut.core.mvi.BaseViewModel
import com.discut.manga.ui.reader.domain.ReaderActivityEffect
import com.discut.manga.ui.reader.domain.ReaderActivityEvent
import com.discut.manga.ui.reader.domain.ReaderActivityState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReaderActivityViewModel @Inject constructor() :
    BaseViewModel<ReaderActivityState, ReaderActivityEvent, ReaderActivityEffect>() {
    override fun initialState(): ReaderActivityState {
        TODO("Not yet implemented")
    }

    override suspend fun handleEvent(
        event: ReaderActivityEvent,
        state: ReaderActivityState
    ): ReaderActivityState? {
        TODO("Not yet implemented")
    }
}
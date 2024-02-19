package com.discut.manga.ui.reader.viewer.domain

import androidx.compose.runtime.mutableStateOf
import com.discut.manga.preference.ReaderMode
import discut.manga.data.chapter.Chapter

class ReaderChapter(
    val dbChapter: Chapter
) {

    private val _stateFlow = mutableStateOf<State>(State.Wait)

    var state
        get() = _stateFlow.value
        set(value) {
            _stateFlow.value = value
        }

    sealed interface State {
        data object Wait : State
        data object Loading : State
        data class Loaded(val pages: List<ReaderPage>) : State

        data class ReLoaded(val prevState: State) : State
        data class Error(val error: Throwable) : State
    }

}
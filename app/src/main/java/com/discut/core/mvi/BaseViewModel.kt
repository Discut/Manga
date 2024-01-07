package com.discut.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : UiState, E : UiEvent, F : UiEffect> : ViewModel() {

    private val initialState: S by lazy { initialState() }

    protected abstract fun initialState(): S

    private val _uiState: MutableStateFlow<S> by lazy { MutableStateFlow(initialState) }

    val uiState: StateFlow<S> by lazy { _uiState }

    private val _uiEvent: MutableSharedFlow<E> = MutableSharedFlow()

    private val _uiEffect: MutableSharedFlow<F> = MutableSharedFlow()

    val uiEffect: Flow<F> = _uiEffect

    init {
        subscribeEvents()
    }

    protected abstract suspend fun handleEvent(event: E, state: S): S?

    protected open fun isSyncReduceEvent(): Boolean = true

    /**
     * 收集事件
     */
    private fun subscribeEvents() {
        //
        viewModelScope.launch {
            _uiEvent.collect {
                reduceEvent(_uiState.value, it)
            }
        }
    }

    /**
     * 发送事件
     */
    fun sendEvent(event: E) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    fun sendEvent(eventBuild: () -> E) {
        sendEvent(eventBuild())
    }

    /**
     * 发送effect
     */
    protected fun sendEffect(effect: F) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }


    internal fun sendState(newState: S.() -> S) {
        _uiState.value = uiState.value.newState()
    }

    /**
     * 处理事件，更新状态
     * @param state S
     * @param event E
     */
    private suspend fun reduceEvent(state: S, event: E) {
        if (isSyncReduceEvent()) {
            viewModelScope.launch {
                handleEvent(event, state)?.let { newState -> sendState { newState } }
            }
        } else {
            handleEvent(event, state)?.let { newState -> sendState { newState } }
        }
    }


}
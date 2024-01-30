package com.discut.manga.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.discut.core.mvi.BaseViewModel
import com.discut.core.mvi.collectEffect
import com.discut.core.mvi.collectState
import com.discut.core.mvi.contract.UiEffect
import com.discut.core.mvi.contract.UiEvent
import com.discut.core.mvi.contract.UiState
import com.discut.manga.receiver.ExtensionChangeEventDelegate
import com.discut.manga.receiver.ExtensionChangeEventDelegateImpl
import com.discut.manga.ui.security.SecurityActivityDelegate
import com.discut.manga.ui.security.SecurityActivityDelegateImpl

abstract class BaseActivity : AppCompatActivity(),
    SecurityActivityDelegate by SecurityActivityDelegateImpl(),
    ExtensionChangeEventDelegate by ExtensionChangeEventDelegateImpl(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isAutoRegisterSecurityActivity()) {
            registerSecurityActivity(this)
        }
    }

    /**
     * Judge whether to automatically register the security activity
     */
    protected open fun isAutoRegisterSecurityActivity(): Boolean = true

    /**
     * Collect, handle state in Activity from ViewModel
     */
    fun <S : UiState, E : UiEvent, F : UiEffect>
            BaseViewModel<S, E, F>.collectState(
        lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
        handleState: (suspend (state: S) -> Unit),
    ) {
        collectState(lifecycle, lifecycleState, handleState)
    }

    /**
     * Collect, reduce effect in Activity from ViewModel
     */
    fun <S : UiState, E : UiEvent, F : UiEffect>
            BaseViewModel<S, E, F>.collectEffect(
        lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
        sideEffect: (suspend (sideEffect: F) -> Unit),
    ) {
        collectEffect(lifecycle, lifecycleState, sideEffect)
    }
}
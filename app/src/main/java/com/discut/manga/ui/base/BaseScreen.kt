package com.discut.manga.ui.base

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel

abstract class BaseScreen<V : ViewModel> {
    @Composable
    fun Content() {
        Content(getViewModel())
    }

    @Composable
    protected abstract fun Content(viewModel: V)

    @Composable
    abstract fun getViewModel(): V
}
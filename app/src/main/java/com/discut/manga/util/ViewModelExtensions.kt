package com.discut.manga.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope

fun ViewModel.launchIO(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launchIO {
        block(this)
    }
}
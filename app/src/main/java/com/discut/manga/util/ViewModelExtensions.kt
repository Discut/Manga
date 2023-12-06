package com.discut.manga.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

fun ViewModel.launchIO(block: suspend () -> Unit) {
    viewModelScope.launchIO {
        block()
    }
}
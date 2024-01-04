package com.discut.manga.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

fun ViewModel.launchIO(block: suspend CoroutineScope.() -> Unit): Job =
    viewModelScope.launchIO {
        block(this)
    }

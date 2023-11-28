package com.discut.manga.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend inline fun <T> withIOContext(noinline block: suspend CoroutineScope.() -> T) = withContext(
    Dispatchers.IO,
    block,
)

suspend inline fun <T> withUIContext(noinline block: suspend CoroutineScope.() -> T) = withContext(
    Dispatchers.Main,
    block,
)
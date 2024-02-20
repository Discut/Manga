package com.discut.manga.ui.util

fun Any?.isNull() = this == null

class AnyOptional<T>(private val value: T?) {
    private var whenNotNull: T.() -> Unit = {}
    private var whenNull: () -> Unit = {}
    fun or(block: T.() -> Unit) {
        whenNotNull = block
        invoke()
    }

    fun whenNull(block: () -> Unit) {
        whenNull = block
    }

    fun invoke() {
        if (value.isNull()) {
            whenNull()
        } else {
            value!!.whenNotNull()
        }
    }
}

fun <T> T?.whenNull(block: () -> Unit): AnyOptional<T> {
    return AnyOptional(this).apply {
        whenNull(block)
    }
}
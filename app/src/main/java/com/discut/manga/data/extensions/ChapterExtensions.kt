package com.discut.manga.data.extensions

import discut.manga.data.chapter.Chapter
import kotlin.math.absoluteValue

fun Chapter.shouldRead(): Boolean {
    return (pagesCount - lastPageRead).absoluteValue <= 1
}
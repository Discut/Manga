package com.discut.manga.data

import discut.manga.data.chapter.Chapter
import managa.source.domain.SChapter
import kotlin.math.absoluteValue

fun Chapter.shouldRead(): Boolean {
    return (pagesCount - lastPageRead).absoluteValue <= 1
}

fun SChapter.toChapter(): Chapter {
    return Chapter.create().copy(
        name = name,
        url = url,
        dateUpload = date_upload,
        chapterNumber = chapter_number.toDouble(),
        scanlator = scanlator?.ifBlank { null }?.trim(),
    )
}

fun Chapter.shouldUpdate(other: Chapter): Boolean {
    return scanlator != other.scanlator ||
            name != other.name ||
            dateUpload != other.dateUpload ||
            chapterNumber != other.chapterNumber ||
            sourceOrder != other.sourceOrder
}

fun Collection<Chapter>.sortedByChapterNumber(): List<Chapter> =
    sortedBy { it.chapterNumber }

fun Chapter.generateHashKey(): String =
    "${name}${id}${url}".generateHashKey()

fun Chapter.toSChapter(): SChapter =
    SChapter.create().let {
        it.url = this.url
        it.chapter_number = this.chapterNumber.toFloat()
        it.name = this.name
        it.scanlator = this.scanlator
        it.date_upload = this.dateUpload
        it
    }


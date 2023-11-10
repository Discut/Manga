package com.discut.manga.ui.reader.viewer.domain

import java.io.InputStream

sealed class PageType {
    sealed class ChapterPage(
        val index: Int,
        url: String = "",
        imageUrl: String? = null,
        var streamGetter: ((Int) -> InputStream)? = null
    ) : PageType()

    sealed class ChapterTransition : PageType()
}


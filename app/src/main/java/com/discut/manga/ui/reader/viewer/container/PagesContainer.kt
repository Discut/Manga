package com.discut.manga.ui.reader.viewer.container

import android.content.Context

interface PagesContainer<A,C> {

    var adapter: A

    var isVisible: Boolean
    fun createContainer(context: Context): C
}
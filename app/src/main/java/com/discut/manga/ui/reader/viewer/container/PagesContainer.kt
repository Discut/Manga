package com.discut.manga.ui.reader.viewer.container

import android.content.Context

abstract class PagesContainer<A,C> {

    abstract var adapter: A

    abstract var isVisible: Boolean
    protected abstract fun createContainer(context: Context): C
}
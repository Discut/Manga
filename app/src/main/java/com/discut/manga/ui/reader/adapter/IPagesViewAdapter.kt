package com.discut.manga.ui.reader.adapter

import android.view.View
import android.view.ViewGroup

interface IPagesViewAdapter {

    fun createPageView(container: ViewGroup, position: Int): View

    fun destroyPageView(container: ViewGroup, position: Int, view: View)

}
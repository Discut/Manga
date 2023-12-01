package com.discut.manga.ui.reader.viewer

import android.annotation.SuppressLint
import android.content.Context
import androidx.viewpager.widget.DirectionalViewPager

@SuppressLint("ViewConstructor")
open class PagesView(context: Context, isHorizontal: Boolean = true) :
    DirectionalViewPager(context, isHorizontal)
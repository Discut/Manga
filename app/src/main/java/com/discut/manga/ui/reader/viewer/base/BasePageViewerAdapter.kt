package com.discut.manga.ui.reader.viewer.base

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

abstract class BasePageViewerAdapter : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = createPageView(container, position)
        container.addView(view)
        return view
    }

    abstract fun createPageView(container: ViewGroup, position: Int): View

    abstract fun destroyPageView(container: ViewGroup, position: Int, view: View)
    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        val view = obj as View
        destroyPageView(container, position, view)
        container.removeView(view)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }
}
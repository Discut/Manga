package com.discut.manga.ui.reader.viewer

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import com.discut.manga.R
import com.discut.manga.ui.reader.viewer.base.BasePageViewerAdapter
import com.discut.manga.ui.reader.viewer.domain.ReaderPage
import com.discut.manga.ui.reader.viewer.domain.PageState
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class PageViewerAdapter(context: Context) : BasePageViewerAdapter() {

    var readerPages: MutableList<ReaderPage> = mutableListOf(
/*        ReaderPage.ChapterPage(1, "", null, null).apply {
            loadPage={
                var stream: InputStream = ByteArrayInputStream(byteArrayOf())
                try {
                    val drawable = context.resources.getDrawable(R.drawable.image_test, null)
                    val toBitmap = drawable.toBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
                    val baos = ByteArrayOutputStream()
                    toBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                    stream = ByteArrayInputStream(baos.toByteArray())
                } catch (e: Exception) {
                    state = PageState.ERROR
                }
                streamGetter = { stream }
                state = PageState.READY
            }

        }*/

    )
        private set

    override fun getCount(): Int {
        return readerPages.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun createPageView(container: ViewGroup, position: Int): View {
        return when (val page = readerPages[position]) {
            is ReaderPage.ChapterPage -> {
                ChapterPageHolder(container.context, page).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }

            is ReaderPage.ChapterTransition -> TextView(container.context).apply {

            }

            else -> throw NotImplementedError("Unsupported page type: $page")
        }
    }

    override fun destroyPageView(container: ViewGroup, position: Int, view: View) {
        // TODO("Not yet implemented")
    }

    override fun getItemPosition(obj: Any): Int {
        val position = readerPages.indexOf(obj as ReaderPage)
        if (position != -1) {
            return position
        }
        return POSITION_NONE
    }
}
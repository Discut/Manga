package com.discut.manga.ui.reader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.viewModels
import com.discut.manga.R
import com.discut.manga.ui.base.BaseActivity
import com.discut.manga.ui.reader.viewer.PageViewer
import com.discut.manga.ui.reader.viewer.PageViewerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReaderActivity : BaseActivity() {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, ReaderActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val vm: ReaderActivityViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reader_layout)

        val findViewById = findViewById<FrameLayout>(R.id.page_container)
        val pageViewer = PageViewer(this, false)
        val pageViewerAdapter = PageViewerAdapter()
        pageViewer.adapter = pageViewerAdapter
        findViewById.addView(pageViewer)
    }
}
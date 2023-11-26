package com.discut.manga.ui.reader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.discut.manga.R
import com.discut.manga.ui.base.BaseActivity
import com.discut.manga.ui.reader.viewer.PageViewer
import com.discut.manga.ui.reader.viewer.PageViewerAdapter
import com.discut.manga.util.withUIContext
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        vm.uiState

        val findViewById = findViewById<FrameLayout>(R.id.page_container)
        val pageViewer = PageViewer(this, false)
        val pageViewerAdapter = PageViewerAdapter(this)
        findViewById.addView(pageViewer)
        pageViewer.adapter = pageViewerAdapter

        val manga = intent.extras?.getLong("manga", -1) ?: -1L
        val chapter = intent.extras?.getLong("chapter", -1) ?: -1L
        if (manga == -1L || chapter == -1L) {
            finish()
            return
        }


        lifecycleScope.launch(Dispatchers.IO) {
            withContext(NonCancellable) {
                vm.init(manga, chapter)
                vm.uiState.collect {
                    withUIContext {
                        when(it){

                        }
                    }
                }
            }
        }
    }
}
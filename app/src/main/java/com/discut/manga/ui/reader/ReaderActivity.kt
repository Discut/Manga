package com.discut.manga.ui.reader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.discut.manga.R
import com.discut.manga.ui.base.BaseActivity
import com.discut.manga.ui.reader.adapter.RecyclerPagesViewAdapter
import com.discut.manga.ui.reader.domain.ReaderActivityEffect
import com.discut.manga.ui.reader.domain.ReaderActivityEvent
import com.discut.manga.ui.reader.viewer.container.VerticalPagesContainer
import com.discut.manga.ui.reader.viewer.domain.ReaderChapter
import com.discut.manga.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ReaderActivity : BaseActivity() {

    //lateinit var pageViewer: PageViewer

    companion object {
        fun startActivity(context: Context, manga: Long, chapter: Long) {
            val intent = Intent(context, ReaderActivity::class.java)
            intent.apply {
                putExtra("manga", manga)
                putExtra("chapter", chapter)
            }
            context.startActivity(intent)
        }
    }

    private val vm: ReaderViewModel by viewModels()

    // private lateinit var pagesContainer: PagesContainer<IPagesViewAdapter, View>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reader_layout)

        //pagesContainer = HorizontalPagesContainer(vm, this)

        //val findViewById = findViewById<FrameLayout>(R.id.page_container)
        //pageViewer = PageViewer(this, false)
        //val pageViewerAdapter = PageViewerAdapter(this)
        //findViewById.addView(pageViewer)
        //pageViewer.adapter = pageViewerAdapter

        val manga = intent.extras?.getLong("manga", -1) ?: -1L
        val chapter = intent.extras?.getLong("chapter", -1) ?: -1L
        if (manga == -1L || chapter == -1L) {
            finish()
            return
        }
        handleUiState()
        handleEffect()

        lifecycleScope.launch(Dispatchers.IO) {
            withContext(NonCancellable) {
                vm.sendEvent(ReaderActivityEvent.Initialize(manga, chapter))
            }
        }
    }

    private fun handleUiState() {
        vm.collectState {
            when (val chapterState = it.currentChapters?.currReaderChapter?.state) {
                is ReaderChapter.State.Error -> {
                    toast(chapterState.error.message)
                    finish()
                }

                is ReaderChapter.State.Loaded -> {
                    val horizontalPagesContainer = VerticalPagesContainer(vm, this)
                    val pageViewerAdapter = RecyclerPagesViewAdapter(this, chapterState.pages)
                    horizontalPagesContainer.adapter = pageViewerAdapter
                    horizontalPagesContainer.isVisible = true
                    //pageViewer.adapter = pageViewerAdapter
                }

                ReaderChapter.State.Loading -> {

                }

                ReaderChapter.State.Wait -> {

                }

                else -> {}
            }
        }
    }

    private fun handleEffect() {
        vm.collectEffect {
            when (it) {
                is ReaderActivityEffect.InitChapterError -> {
                    Log.d("ReaderActivity", it.error.message.orEmpty())
                    toast(it.error.message)
                    finish()
                }

                else -> {}
            }
        }
    }
}
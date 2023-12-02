package com.discut.manga.ui.reader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.discut.manga.R
import com.discut.manga.ui.base.BaseActivity
import com.discut.manga.ui.reader.adapter.RecyclerPagesViewAdapter
import com.discut.manga.ui.reader.component.ReaderNavigationBar
import com.discut.manga.ui.reader.domain.ReaderActivityEffect
import com.discut.manga.ui.reader.domain.ReaderActivityEvent
import com.discut.manga.ui.reader.viewer.container.VerticalPagesContainer
import com.discut.manga.ui.reader.viewer.domain.ReaderChapter
import com.discut.manga.util.setComposeContent
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
    private val windowInsetsController by lazy {
        WindowInsetsControllerCompat(
            window,
            window.decorView
        )
    }


    // private lateinit var pagesContainer: PagesContainer<IPagesViewAdapter, View>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reader_layout)
        initMenuView()

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

    private fun initMenuView() {
        val menuRoot = findViewById<ComposeView>(R.id.menu_root)

        menuRoot.setComposeContent {
            val state by vm.uiState.collectAsState()

            ReaderNavigationBar(
                visibility = state.isMenuShow,
                mangaTitle = state.manga?.title,
                chapterTitle = state.currentChapters?.currReaderChapter?.dbChapter?.name
            ) {
                finish()
            }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (vm.uiState.value.isMenuShow) {
            vm.sendEvent(ReaderActivityEvent.ReaderNavigationMenuVisibleChange(false))
            return
        }
        super.onBackPressed()
    }

    private fun handleUiState() {
        var oldChapterState: ReaderChapter.State? = null
        vm.collectState {
            val chapterState = it.currentChapters?.currReaderChapter?.state
            if (oldChapterState != chapterState) {
                oldChapterState = chapterState
                handleChapterStateChange(chapterState)
            }
            handleMenuStateChange(it.isMenuShow)
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

    private fun handleChapterStateChange(chapterState: ReaderChapter.State?) {
        when (chapterState) {
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

    private fun handleMenuStateChange(visible: Boolean) {
        if (visible) {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        } else {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
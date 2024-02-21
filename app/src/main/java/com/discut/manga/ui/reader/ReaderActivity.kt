package com.discut.manga.ui.reader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.discut.manga.R
import com.discut.manga.preference.ReaderMode
import com.discut.manga.ui.base.BaseActivity
import com.discut.manga.ui.common.LoadingScreen
import com.discut.manga.ui.main.MainActivity
import com.discut.manga.ui.reader.viewer.horizontal.HorizontalPageViewerAdapter
import com.discut.manga.ui.reader.viewer.vertical.VerticalPagesViewAdapter
import com.discut.manga.ui.reader.component.BottomSheetMenu
import com.discut.manga.ui.reader.component.ReaderModeSheetMenu
import com.discut.manga.ui.reader.component.ReaderNavigationBar
import com.discut.manga.ui.reader.domain.CurrentChapters
import com.discut.manga.ui.reader.domain.ReaderActivityEffect
import com.discut.manga.ui.reader.domain.ReaderActivityEvent
import com.discut.manga.ui.reader.viewer.horizontal.HorizontalPagesContainer
import com.discut.manga.ui.reader.viewer.PagesContainer
import com.discut.manga.ui.reader.viewer.vertical.VerticalPagesContainer
import com.discut.manga.ui.reader.domain.ReaderChapter
import com.discut.manga.ui.reader.domain.ReaderPage
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
    private var pagesContainer: PagesContainer? = null

    companion object {
        fun startActivity(
            context: Context,
            manga: Long,
            chapter: Long,
            onBack: (Long) -> Unit = {}
        ) {
            val intent = Intent(context, ReaderActivity::class.java)
            intent.apply {
                putExtra("manga", manga)
                putExtra("chapter", chapter)
            }
            MainActivity.buildLauncher(onBack).launch(intent)
            //context.startActivity(intent)
        }

        const val LAUNCH_MANGA_DETAILS_CODE = 0x01
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

    @OptIn(ExperimentalMaterial3Api::class)
    private fun initMenuView() {
        val menuRoot = findViewById<ComposeView>(R.id.menu_root)


        menuRoot.setComposeContent {
            val scope = rememberCoroutineScope()
            val state by vm.uiState.collectAsState()
            var showBottomSheet by remember { mutableStateOf(false) }
            var showReaderModeSheet by remember { mutableStateOf(false) }

            ReaderNavigationBar(
                visibility = state.isMenuShow,
                mangaTitle = state.manga?.title,
                chapterTitle = state.currentChapters?.currReaderChapter?.dbChapter?.name,
                readerMode = state.readerMode,

                currentPage = state.currentPage,
                pageCount = state.readerPages,
                enableNextChapter = state.currentChapters?.nextReaderChapter != null,
                enablePreviousChapter = state.currentChapters?.prevReaderChapter != null,
                onSliderChange = {
                    pagesContainer?.moveToPage(it)
                },
                onSliderChangeFinished = {
                },
                onNextChapter = {

                },
                onPreviousChapter = {

                },
                onClickReaderMode = {
                    showReaderModeSheet = true
                },
                onClickSettings = {
                    showBottomSheet = true
                },

                onBackActionClick = {
                    finish()
                },
                onMangaTitleClick = {
                    Intent().apply {
                        putExtra("mangaId", state.manga?.id)
                    }.also {
                        setResult(LAUNCH_MANGA_DETAILS_CODE, it)
                        finish()
                    }
                }
            )

            BottomSheetMenu(isShow = showBottomSheet) {
                showBottomSheet = false
            }
            ReaderModeSheetMenu(
                isShow = showReaderModeSheet,
                readerMode = state.readerMode,
                onReaderModeChange = {
                    vm.sendEvent(ReaderActivityEvent.ReaderModeChange(it))
                    if (state.currentChapters?.currReaderChapter?.state is ReaderChapter.State.Loaded) {
                        buildPagesContainerAndShow(state.currentChapters!!.pages, it, true)
                    }
                }) {
                showReaderModeSheet = false
            }
            if (state.currentChapters == null ||
                state.currentChapters?.currReaderChapter?.state is ReaderChapter.State.Loading ||
                state.currentChapters?.currReaderChapter?.state is ReaderChapter.State.Wait
            ) {
                LoadingScreen(placeholderText = "Loading...")
            }
        }


    }

    /*    @Deprecated("Deprecated in Java")
        override fun onBackPressed() {
            if (vm.uiState.value.isMenuShow) {
                vm.sendEvent(ReaderActivityEvent.ReaderNavigationMenuVisibleChange(false))
                return
            }
            super.onBackPressed()
        }*/

    private fun handleUiState() {
        /*        var oldChapterState: ReaderChapter.State? = null
                vm.collectState {
                    val currentChapters = it.currentChapters
                    val chapterState = currentChapters?.currReaderChapter?.state
                    if (oldChapterState != chapterState) {
                        oldChapterState = chapterState
                        handleChapterStateChange(chapterState, it.readerMode)
                    }
                    handleMenuStateChange(it.isMenuShow)
                }*/
        var oldCurrentChapters: CurrentChapters? = null
        vm.collectState {
            it.currentChapters?.let { currentChapters ->
                val offset =
                    if (currentChapters.prevReaderChapter?.state is ReaderChapter.State.Loaded) {
                        3
                    } else -1
                if (currentChapters != oldCurrentChapters) {
                    handleChapterStateChange(
                        currentChapters.currReaderChapter.state,
                        currentChapters.pages,
                        offset,
                        it.readerMode
                    )
                }
                oldCurrentChapters = currentChapters
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
                    Log.e("ReaderActivity", it.error.message.orEmpty(), it.error)
                    finish()
                }

                else -> {}
            }
        }
    }

    private fun handleChapterStateChange(
        chapterState: ReaderChapter.State?,
        allPages: List<ReaderPage>,
        offset: Int,
        readerMode: ReaderMode
    ) {
        when (chapterState) {
            is ReaderChapter.State.Error -> {
                toast(chapterState.error.message)
                finish()
            }

            is ReaderChapter.State.Loaded -> buildPagesContainerAndShow(
                allPages,
                readerMode,
                false,
                offset
            )//showPages(chapterState.pages, readerMode)

            ReaderChapter.State.Loading -> {

            }

            ReaderChapter.State.Wait -> {

            }

            else -> {}
        }
    }

    /**
     * Show pages of loaded on screen.
     */
    private fun buildPagesContainerAndShow(
        pages: List<ReaderPage>,
        readerMode: ReaderMode,
        isForce: Boolean = false,
        offset: Int = -1
    ) {
        if (isForce.not() && pagesContainer != null) {
            resetPages(pages, offset)
            return
        }

        pagesContainer?.destroy()
        when (readerMode) {
            ReaderMode.WEBTOON -> {
                val verticalPagesContainer = VerticalPagesContainer(vm, this)
                pagesContainer = verticalPagesContainer
                val pageViewerAdapter = VerticalPagesViewAdapter(this, pages)
                verticalPagesContainer.adapter = pageViewerAdapter
                if (offset > 0 && offset < pageViewerAdapter.itemCount) {
                    verticalPagesContainer.moveToPage(offset)
                }
                verticalPagesContainer.isVisible = true
                //pageViewer.adapter = pageViewerAdapter

            }

            ReaderMode.LEFT_TO_RIGHT -> {
                val horizontalPagesContainer = HorizontalPagesContainer(vm, this)
                pagesContainer = horizontalPagesContainer
                val horizontalPageViewerAdapter = HorizontalPageViewerAdapter(pages)
                horizontalPagesContainer.adapter = horizontalPageViewerAdapter
                if (offset > 0 && offset < horizontalPageViewerAdapter.count) {
                    horizontalPagesContainer.moveToPage(offset)
                }
                horizontalPagesContainer.isVisible = true

            }
        }

    }

    private fun resetPages(pages: List<ReaderPage>, offset: Int = -1) {
        pagesContainer?.apply {
            setPages(pages)
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
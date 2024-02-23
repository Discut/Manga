package com.discut.manga.ui.reader.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.discut.manga.components.CustomModalBottomSheet
import com.discut.manga.preference.ReaderBackgroundColor
import com.discut.manga.preference.ReaderMode
import com.discut.manga.theme.padding
import com.discut.manga.util.launchUI

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BottomSheetMenu(
    modifier: Modifier = Modifier,
    isShow: Boolean = false,
    isScreenOn: Boolean = false,
    readerMode: ReaderMode,
    backgroundColor: ReaderBackgroundColor,

    sheetState: SheetState = rememberModalBottomSheetState(),
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,

    onReaderModeChange: (ReaderMode) -> Unit,
    onScreenOnChange: (Boolean) -> Unit,
    onBackgroundColorChange: (ReaderBackgroundColor) -> Unit,
    onDismissRequest: () -> Unit
) {
    CustomModalBottomSheet(
        modifier = modifier,
        isShow = isShow,
        sheetState = sheetState,
        sheetMaxWidth = sheetMaxWidth,
        isCustomPadding = true,
        onDismissRequest = onDismissRequest
    ) {
        val pages = listOf("Normal", "Read mode")
        val scope = rememberCoroutineScope()
        val state = rememberPagerState {
            pages.size
        }
        BoxWithConstraints {
            Column(
                modifier = Modifier
                    .height(maxHeight * 0.75f)
                    .verticalScroll(rememberScrollState())
                    .padding(it)
            ) {
                TabRow(
                    selectedTabIndex = state.currentPage,
                    indicator = {
                        TabRowDefaults.PrimaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(it[state.currentPage]),
                        )
                    }
                ) {
                    pages.forEachIndexed { index, s ->
                        Tab(
                            selected = state.currentPage == index,
                            onClick = {
                                scope.launchUI {
                                    state.animateScrollToPage(index)
                                }
                            },
                        ) {
                            CompositionLocalProvider(
                                LocalContentColor provides
                                        if (state.currentPage != index) {
                                            Color.Unspecified
                                        } else {
                                            LocalContentColor.current
                                        }
                            ) {
                                Text(
                                    text = s,
                                    modifier = Modifier.padding(vertical = MaterialTheme.padding.Default)
                                )
                            }
                        }
                    }
                }
                HorizontalPager(state = state) {
                    when (pages[it]) {
                        "Normal" -> {
                            NormalSettingsSheet()
                        }

                        "Read mode" -> {
                            ReadModeSettingsSheet(
                                readerMode = readerMode,
                                isScreenOn = isScreenOn,
                                backgroundColor = backgroundColor,
                                onBackgroundColorChange = onBackgroundColorChange,
                                onReaderModeChange = onReaderModeChange,
                                onScreenOnChange = onScreenOnChange
                            )
                        }

                        else -> {
                            Text(text = "Error")
                        }
                    }
                }
            }
        }
    }
}
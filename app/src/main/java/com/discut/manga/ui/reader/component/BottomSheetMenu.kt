package com.discut.manga.ui.reader.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import com.discut.manga.theme.padding
import com.discut.manga.util.launchUI

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BottomSheetMenu(
    modifier: Modifier = Modifier,
    isShow: Boolean = false,
    sheetState: SheetState = rememberModalBottomSheetState(),
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,

    onDismissRequest: () -> Unit
) {
    CustomModalBottomSheet(
        modifier = modifier,
        isShow = isShow,
        sheetState = sheetState,
        sheetMaxWidth = sheetMaxWidth,
        onDismissRequest = onDismissRequest
    ) {
        val pages = listOf("Normal", "Read mode")
        val scope = rememberCoroutineScope()
        val state = rememberPagerState {
            pages.size
        }
        BoxWithConstraints {
            Column(
                modifier = Modifier.heightIn(min = maxHeight * 0.5f)
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
                            ReadModeSettingsSheet()
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
package com.discut.manga.ui.reader.component

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

@Composable
fun ReaderNavigationBottomBar(
    modifier: Modifier = Modifier,
    currentPage: Int,
    pageCount: Int,
    enableNextChapter: Boolean,
    enablePreviousChapter: Boolean,
    onSliderChange: (Int) -> Unit,
    onNextChapter: () -> Unit,
    onPreviousChapter: () -> Unit,
    onClickSettings: () -> Unit,
    onSliderChangeFinished: (() -> Unit)? = null
) {
    val backgroundColor = MaterialTheme.colorScheme
        .surfaceColorAtElevation(3.dp)
        .copy(alpha = if (isSystemInDarkTheme()) 0.9f else 0.95f)
    val buttonColor = IconButtonDefaults.filledIconButtonColors(
        containerColor = backgroundColor,
        disabledContainerColor = backgroundColor,
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        ChapterPageNavigator(
            currentPage = currentPage,
            pageCount = pageCount,
            enableNextChapter = enableNextChapter,
            enablePreviousChapter = enablePreviousChapter,
            buttonColor = buttonColor,
            backgroundColor = backgroundColor,
            onSliderChange = onSliderChange,
            onNextChapter = onNextChapter,
            onPreviousChapter = onPreviousChapter,
            onValueChangeFinished = onSliderChangeFinished
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .navigationBarsPadding()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            IconButton(onClick = onClickSettings) {
                Icon(
                    imageVector = Icons.Outlined.Timelapse,
                    contentDescription = "Autoplay",
                )
            }
            IconButton(onClick = onClickSettings) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                )
            }
        }
    }
}
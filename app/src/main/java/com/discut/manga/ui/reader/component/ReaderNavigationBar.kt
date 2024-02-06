package com.discut.manga.ui.reader.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderNavigationBar(
    visibility: Boolean = false,
    mangaTitle: String?,
    chapterTitle: String?,

    currentPage: Int,
    pageCount: Int,
    enableNextChapter: Boolean,
    enablePreviousChapter: Boolean,
    onSliderChange: (Int) -> Unit,
    onSliderChangeFinished: (() -> Unit)? = null,
    onNextChapter: () -> Unit,
    onPreviousChapter: () -> Unit,
    onClickSettings: () -> Unit,

    onBackActionClick: () -> Unit,
    onMangaTitleClick: () -> Unit
) {

    val backgroundColor = MaterialTheme.colorScheme
        .surfaceColorAtElevation(3.dp)
        .copy(alpha = BACKGROUND_COLOR_ALPHA/*getBackgroundColorAlpha()*/)

    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ComponentSlideDownAndUp(
            visibility = visibility
        ) {
            ReaderNavigationTopBar(
                title = mangaTitle,
                subtitle = chapterTitle,
                onBackActionClick = onBackActionClick,
                backgroundColor = backgroundColor,

                onTitleClick = onMangaTitleClick
            )
        }
        Spacer(modifier = Modifier.weight(1f))// Context Space
        ComponentSlideUpAndDown(
            visibility = visibility
        ) {
            ReaderNavigationBottomBar(
                currentPage = currentPage,
                pageCount = pageCount,
                enableNextChapter = enableNextChapter,
                enablePreviousChapter = enablePreviousChapter,
                onSliderChange = onSliderChange,
                onNextChapter = onNextChapter,
                onPreviousChapter = onPreviousChapter,
                onClickSettings = onClickSettings,
                onSliderChangeFinished = onSliderChangeFinished
            )
        }
    }

}

@Composable
private fun ComponentSlideDownAndUp(
    visibility: Boolean = false,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visibility,
        content = content,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = animationFunction,
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = animationFunction,
        )
    )
}

@Composable
private fun ComponentSlideUpAndDown(
    visibility: Boolean = false,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visibility,
        content = content,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = animationFunction,
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = animationFunction,
        )
    )
}

/*
@Composable
private fun getBackgroundColorAlpha(): Float {
    return if (isSystemInDarkTheme()) 0.9f else 0.95f
}*/


private const val BACKGROUND_COLOR_ALPHA = 0.9F

private val animationFunction = tween<IntOffset>(200)
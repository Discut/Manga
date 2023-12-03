package com.discut.manga.ui.reader.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun ChapterPageNavigator(
    currentPage: Int,
    pageCount: Int,
    enableNextChapter: Boolean = true,
    enablePreviousChapter: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
    buttonColor: IconButtonColors = IconButtonDefaults.filledIconButtonColors(
        containerColor = backgroundColor,
        disabledContainerColor = backgroundColor,
    ),
    onSliderChange: (Int) -> Unit,
    onNextChapter: () -> Unit,
    onPreviousChapter: () -> Unit,
    onValueChangeFinished: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        IconButton(
            enabled = enablePreviousChapter,
            onClick = onPreviousChapter,
            colors = buttonColor
        ) {
            Icon(
                imageVector = Icons.Outlined.SkipPrevious,
                contentDescription = null
            )
        }
        Text(text = currentPage.toString())
        Slider(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            value = currentPage.toFloat(),
            valueRange = 1f..pageCount.toFloat(),
            steps = pageCount /*- 2*/,
            onValueChange = {
                onSliderChange(it.roundToInt() - 1)
            },
            onValueChangeFinished = onValueChangeFinished
        )

        Text(text = pageCount.toString())
        IconButton(
            enabled = enableNextChapter,
            onClick = onNextChapter,
            colors = buttonColor
        ) {
            Icon(
                imageVector = Icons.Outlined.SkipNext,
                contentDescription = null
            )
        }
    }
}
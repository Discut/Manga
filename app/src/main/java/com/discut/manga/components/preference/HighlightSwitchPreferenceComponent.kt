package com.discut.manga.components.preference

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discut.manga.theme.MangaTheme

@Composable
fun HighlightSwitchPreferenceComponent(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    title: String
) {
    BasePreferenceComponent(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.extraLarge
            )
            .padding(vertical = 8.dp)
            .padding(start = 6.dp),
        title = title,
        titleFontSize = LabelFontSize,
        endWidget = {
            Switch(
                checked = checked,
                onCheckedChange = null
            )
        },
        onClick = { onCheckedChange?.invoke(!checked) }
    )
}


@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color,
) = Canvas(
    modifier
        .fillMaxHeight()
        .width(thickness)
) {
    drawLine(
        color = color,
        strokeWidth = thickness.toPx(),
        start = Offset(thickness.toPx() / 2, 0f),
        end = Offset(thickness.toPx() / 2, size.height),
    )
}

internal val LabelFontSize = 20.sp

@Preview(
    showBackground = true,
    name = "Light"
)
@Composable
private fun HighlightSwitchPreferenceComponentPreview() {
    var checked by remember {
        mutableStateOf(false)
    }
    MangaTheme {
        Box() {
            HighlightSwitchPreferenceComponent(
                modifier = Modifier,
                title = "开启“自动适应亮度”",
                checked = checked,
                onCheckedChange = {
                    checked = it
                }
            )
        }

    }
}
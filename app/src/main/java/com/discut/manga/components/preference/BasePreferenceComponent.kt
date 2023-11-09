package com.discut.manga.components.preference

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discut.manga.theme.MangaTheme

@Composable
fun BasePreferenceComponent(
    modifier: Modifier = Modifier,
    title: String = "",
    titleFontSize: TextUnit = 16.sp,
    onClick: (() -> Unit)? = null,
    subWidget: @Composable (ColumnScope.() -> Unit)? = null,
    iconWidget: @Composable (() -> Unit)? = null,
    endWidget: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .clickable(enabled = onClick != null, onClick = { onClick?.invoke() })
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconWidget != null) {
            Box(modifier = Modifier.padding(start = 16.dp/*, end = 8.dp*/)) {
                iconWidget()
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp)
        ) {
            if (title.isNotBlank()) {
                Text(
                    text = title,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = titleFontSize
                )
            }
            subWidget?.invoke(this)
        }
        if (endWidget != null) {
            Box(modifier = Modifier.padding(end = 16.dp)) {
                endWidget()
            }
        }
    }
}

@Preview(
    showBackground = true,
    name = "Light",
)
@Composable
private fun BasePreferenceComponentPreview() {
    MangaTheme {
        BasePreferenceComponent(
            title = "Title",
        )
    }
}
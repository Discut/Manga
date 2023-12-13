package com.discut.manga.components.preference

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.sp
import com.discut.manga.theme.MangaTheme
import com.discut.manga.theme.padding

@Composable
fun BasePreferenceComponent(
    modifier: Modifier = Modifier,
    title: String = "",
    titleFontSize: TextUnit = 18.sp,
    onClick: (() -> Unit)? = null,
    subWidget: @Composable (ColumnScope.() -> Unit)? = null,
    iconWidget: @Composable ((PaddingValues) -> Unit)? = null,
    endWidget: @Composable ((PaddingValues) -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .clickable(enabled = onClick != null, onClick = { onClick?.invoke() })
            .padding(vertical = MaterialTheme.padding.Default)
            .then(modifier)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconWidget != null) {
            iconWidget(PaddingValues(end = MaterialTheme.padding.Normal))
        }
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            if (title.isNotBlank()) {
                Text(
                    text = title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = titleFontSize
                )
            }
            subWidget?.invoke(this)
        }
        if (endWidget != null) {
            endWidget(PaddingValues(start = MaterialTheme.padding.Normal))
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
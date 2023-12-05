package com.discut.manga.ui.manga.details.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.discut.manga.theme.alpha
import com.discut.manga.theme.padding

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutBookSheet(
    modifier: Modifier = Modifier,
    description: String,
    chips: List<String>
) {
    Column(modifier = modifier) {
        SmallTitle(title = "简介")
        SmallDescription(description = description)
        Spacer(modifier = Modifier.height(MaterialTheme.padding.Normal))
        FlowRow {
            chips.forEachIndexed { index, s ->
                AssistChip(
                    modifier = Modifier.padding(0.dp),
                    onClick = { /*TODO*/ },
                    label = { SmallDescription(description = s) })
                if (index != chips.size - 1) {
                    Spacer(modifier = Modifier.width(MaterialTheme.padding.Default))
                }
            }

        }
    }
}


@Composable
internal fun SmallTitle(
    title: String
) {
    Text(
        modifier = Modifier.padding(vertical = MaterialTheme.padding.Default),
        text = title,
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
internal fun SmallDescription(
    description: String
) {
    Text(
        text = description,
        modifier = Modifier.alpha(MaterialTheme.alpha.Normal),
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
@PreviewLightDark
internal fun AboutBookSheetPreview() {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AboutBookSheet(
                description = "test",
                chips = strings
            )
        }
    }

}

private val strings = listOf<String>(
    "简介",
    "目录",
    "作者",
    "状态",
    "标签",
    "更新",
    "字数",
    "最新章节",
    "推荐",
    "评论",
    "推荐",
)

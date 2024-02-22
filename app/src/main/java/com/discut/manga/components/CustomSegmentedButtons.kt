package com.discut.manga.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.discut.manga.components.preference.VerticalDivider
import com.discut.manga.theme.MangaTheme

@Composable
fun CustomSegmentedButtons(
    modifier: Modifier = Modifier,
    onActiveItem: Any? = null,
    onActive: (key: Any) -> Unit,
    content: SegmentedButtonsScope.() -> Unit
) {
    val contents = SegmentedButtonsScopeImpl().apply(content)
    if (contents.items.isEmpty()) {
        return
    }
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .border(width = 1.dp, color = Color.LightGray, shape = MaterialTheme.shapes.large)
            .height(IntrinsicSize.Min)

    ) {
        var index = 0
        contents.items.forEach { (key, value) ->
            if (index > 0) {
                VerticalDivider(modifier = Modifier/*.fillMaxHeight()*/, color = Color.LightGray)
            }
            Box(
                modifier = Modifier
                    .background(color = if (key == onActiveItem) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        onActive(key)
                    },
                contentAlignment = Alignment.Center
            ) {
                value()
            }
            index++
        }
    }
}

interface SegmentedButtonsScope {
    fun item(key: Any? = null, content: @Composable () -> Unit)
}

internal class SegmentedButtonsScopeImpl : SegmentedButtonsScope {
    internal val items = mutableMapOf<Any, @Composable () -> Unit>()
    override fun item(key: Any?, content: @Composable () -> Unit) {
        items[key ?: items.size] = content
    }

}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SegmentedButtonsPreview() {
    MangaTheme {
        CustomSegmentedButtons(
            modifier = Modifier
                .padding(vertical = 100.dp, horizontal = 20.dp)
                .fillMaxWidth()
                .height(50.dp),
            onActive = {

            }
        ) {
            item {
                Text(text = "1")
            }
            item {
                Text(text = "1")
            }
            item {
                Text(text = "1")
            }
        }
    }
}

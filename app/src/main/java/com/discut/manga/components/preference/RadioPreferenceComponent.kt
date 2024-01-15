package com.discut.manga.components.preference

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.discut.manga.theme.MangaTheme

@Composable
fun <K> RadioPreferenceComponent(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String? = null,
    icon: ImageVector? = null,
    state: RadioPreferenceState<K>,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    endWidget: @Composable ((PaddingValues) -> Unit)? = null,
    onPreferenceClick: (() -> Unit)? = null,
) {
    var isDialogShow by remember {
        mutableStateOf(false)
    }
    TextPreferenceComponent(
        title = title,
        subTitle = subTitle,
        icon = icon,
        iconTint = iconTint,
        endWidget = endWidget,
        onPreferenceClick = {
            isDialogShow = !isDialogShow
            onPreferenceClick?.invoke()
        }
    )
    if (isDialogShow) {
        AlertDialog(
            title = { Text(text = title) },
            modifier = modifier,
            onDismissRequest = { isDialogShow = false },
            text = {
                Box {
                    // val state = rememberLazyListState()
                    LazyColumn {
                        item {
                            for (i in 0..<state.keys().size) {
                                val key = state.keys()[i]
                                val label = state.getOption(key)
                                val isSelected = state.isSelected(key)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .clip(MaterialTheme.shapes.small)
                                        .selectable(
                                            selected = isSelected,
                                            onClick = {
                                                if (!isSelected) {
                                                    state.onSelected(key, i)
                                                    isDialogShow = false
                                                }
                                            },
                                        )
                                        .padding(vertical = 8.dp)
                                        .fillMaxWidth(),
                                    /*.minimumInteractiveComponentSize()*/
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null,
                                    )
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyLarge.merge(),
                                        modifier = Modifier.padding(start = 24.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { isDialogShow = false }) {
                    Text(text = "取消")
                }
            })
    }
}

interface RadioPreferenceState<K> {
    fun getOption(key: K): String

    fun keys(): List<K>

    fun isSelected(key: K): Boolean

    fun onSelected(key: K, index: Int)
}

@Composable
fun <K> rememberRadioPreferenceState(
    data: Map<K, String>,
    default: K? = null,
    onSelected: (K, Int) -> Unit
): RadioPreferenceState<K> = remember {
    RadioPreferenceStateImpl(
        map = data,
        default = default,
        outerOnSelected = onSelected
    )
}

@Stable
private class RadioPreferenceStateImpl<K>(
    val map: Map<K, String> = emptyMap(),
    val default: K? = null,
    val outerOnSelected: (K, Int) -> Unit
) : RadioPreferenceState<K> {

    private var selected: K? = default
    override fun getOption(key: K): String =
        map[key] ?: ""

    override fun keys(): List<K> = map.keys.toList()

    override fun onSelected(key: K, index: Int) {
        selected = key
        this.outerOnSelected(key, index)
    }

    override fun isSelected(key: K): Boolean = key == selected

}

@Deprecated("Use rememberRadioPreferenceState instead")
interface RadioPreferenceAdapter<K> {
    fun getOption(key: K): String

    fun keys(): List<K>

    fun isSelected(key: K): Boolean

    fun onSelected(key: K, index: Int)
}


@Preview(
    showBackground = true,
    name = "Light"
)
@Composable
private fun RadioPreferenceComponentPreview() {
    var subTitle by remember {
        mutableStateOf("")
    }
    val state = rememberRadioPreferenceState(
        data = mapOf("A" to "A", "B" to "B", "C" to "C"),
        onSelected = { _, _ -> }
    )
    MangaTheme {
        Column {
            RadioPreferenceComponent<String>(
                title = "RadioPreferenceComponent",
                subTitle = subTitle,
                state = state
                /*adapter = object : RadioPreferenceAdapter<String> {
                    val map = mapOf(
                        "A" to "A",
                        "B" to "B",
                        "C" to "C",
                    )

                    override fun getOption(key: String): String {
                        return map[key]!!
                    }

                    override fun keys(): List<String> {
                        return map.keys.toList()
                    }

                    override fun onSelected(key: String, index: Int) {
                        subTitle = getOption(key)
                    }

                    override fun isSelected(key: String): Boolean {
                        return subTitle == getOption(key)
                    }
                }*/
            )
        }
    }
}
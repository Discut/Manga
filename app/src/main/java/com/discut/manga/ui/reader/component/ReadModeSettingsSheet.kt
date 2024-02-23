package com.discut.manga.ui.reader.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.discut.manga.components.CustomSegmentedButtons
import com.discut.manga.preference.ReaderBackgroundColor
import com.discut.manga.preference.ReaderMode
import com.discut.manga.preference.clickNavigationList
import com.discut.manga.theme.padding

@Composable
fun ReadModeSettingsSheet(
    modifier: Modifier = Modifier,
    readerMode: ReaderMode,
    isScreenOn: Boolean,
    backgroundColor: ReaderBackgroundColor,
    onReaderModeChange: (ReaderMode) -> Unit,
    onScreenOnChange: (Boolean) -> Unit,
    onBackgroundColorChange: (ReaderBackgroundColor) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        DefaultSpacer()
        Label(label = "Mode")
        DefaultSpacer()
        ReaderModeSelector(
            modifier = Modifier.padding(horizontal = MaterialTheme.padding.Normal),
            onActiveItem = readerMode,
            onActive = {
                onReaderModeChange(it as ReaderMode)
            })
        DefaultSpacer()
        Label(label = "BackgroundColor")
        DefaultSpacer()
        CustomSegmentedButtons(
            modifier = Modifier.padding(horizontal = MaterialTheme.padding.Normal),
            onActiveItem = backgroundColor,
            onActive = {
                onBackgroundColorChange(it as ReaderBackgroundColor)
            }) {
            ReaderBackgroundColor.entries.forEach {
                item(it) {
                    Text(
                        text = stringResource(id = it.stringRes),
                        modifier = Modifier.padding(vertical = MaterialTheme.padding.Small)
                    )
                }
            }
        }
        DefaultSpacer()
        Label(label = "Navigation")
        DefaultSpacer()
        CustomSegmentedButtons(
            modifier = Modifier.padding(horizontal = MaterialTheme.padding.Normal),
            onActiveItem = backgroundColor,
            onActive = {
                onBackgroundColorChange(it as ReaderBackgroundColor)
            }) {
            clickNavigationList.forEach {
                item(it) {
                    Text(
                        text = it.toString(),
                        modifier = Modifier.padding(vertical = MaterialTheme.padding.Small)
                    )
                }
            }
        }
        DefaultSpacer()
        LabelSwitch(
            checked = isScreenOn,
            label = "屏幕常亮",
            modifier = Modifier.padding(
                horizontal = MaterialTheme.padding.Normal
            ),
            onCheckedChange = onScreenOnChange
        )
        DefaultSpacer()
        LabelSwitch(
            label = "显示页数",
            checked = false,
            modifier = Modifier.padding(
                horizontal = MaterialTheme.padding.Normal
            ),
            onCheckedChange = {})

    }
}
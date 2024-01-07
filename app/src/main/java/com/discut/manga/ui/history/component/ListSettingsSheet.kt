@file:OptIn(ExperimentalMaterial3Api::class)

package com.discut.manga.ui.history.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.discut.manga.components.CustomModalBottomSheet
import com.discut.manga.components.CustomSegmentedButtons
import com.discut.manga.theme.padding
import manga.core.preference.HistoryPreference

@Composable
fun ListSettingsSheet(
    modifier: Modifier = Modifier,
    isShow: Boolean = false,
    sheetState: SheetState = rememberModalBottomSheetState(),
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,

    historyListLayout: HistoryItemType = HistoryItemType.LOOSE,

    onHistoryListLayoutChange: (HistoryItemType) -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    CustomModalBottomSheet(
        modifier = modifier,
        isShow = isShow,
        sheetState = sheetState,
        sheetMaxWidth = sheetMaxWidth,
        onDismissRequest = onDismissRequest
    ) {
        Text(
            text = "List mode",
            modifier = Modifier.padding(
                horizontal = MaterialTheme.padding.Normal,
                vertical = MaterialTheme.padding.Default
            )
        )
        CustomSegmentedButtons(
            modifier = Modifier
                .padding(
                    horizontal = MaterialTheme.padding.Normal,
                    vertical = MaterialTheme.padding.Default
                )
                .height(80.dp),
            onActiveItem  = historyListLayout,
            onActive = {
                when (it) {
                    HistoryItemType.LOOSE -> onHistoryListLayoutChange(
                        HistoryItemType.LOOSE
                    )

                    HistoryItemType.COMPACT -> onHistoryListLayoutChange(
                        HistoryItemType.COMPACT
                    )

                    else -> {}
                }
            }) {
            item(HistoryItemType.LOOSE) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = MaterialTheme.padding.Normal)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatListNumbered,
                        contentDescription = "Loose list"
                    )
                    Text(text = "Loose list")
                }
            }
            item(HistoryItemType.COMPACT) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = MaterialTheme.padding.Normal)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.List,
                        contentDescription = "Compact list"
                    )
                    Text(text = "Compact list")
                }
            }
        }

    }
}
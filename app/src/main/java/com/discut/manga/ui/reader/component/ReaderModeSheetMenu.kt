package com.discut.manga.ui.reader.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.discut.manga.components.CustomModalBottomSheet
import com.discut.manga.components.CustomSegmentedButtons
import com.discut.manga.preference.ReaderMode
import com.discut.manga.theme.padding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderModeSheetMenu(
    modifier: Modifier = Modifier,
    isShow: Boolean = false,
    readerMode: ReaderMode,
    sheetState: SheetState = rememberModalBottomSheetState(),
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,

    onReaderModeChange: (ReaderMode) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    CustomModalBottomSheet(
        modifier = modifier,
        isShow = isShow,
        sheetState = sheetState,
        sheetMaxWidth = sheetMaxWidth,
        onDismissRequest = onDismissRequest
    ) {
        Text(
            text = "Reader mode",
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
            onActiveItem = readerMode,
            onActive = {
                onReaderModeChange(it as ReaderMode)
            }
        ) {
            ReaderMode.entries.forEach {
                item(it) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = MaterialTheme.padding.Normal)
                    ) {
                        Icon(
                            painter = painterResource(id = it.iconRes),
                            contentDescription = stringResource(id = it.stringRes)
                        )
                        Text(text = stringResource(id = it.stringRes))
                    }
                }
            }
        }
    }
}
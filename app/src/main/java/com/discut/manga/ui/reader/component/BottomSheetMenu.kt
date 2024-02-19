package com.discut.manga.ui.reader.component

import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.discut.manga.components.CustomModalBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetMenu(
    modifier: Modifier = Modifier,
    isShow: Boolean = false,
    sheetState: SheetState = rememberModalBottomSheetState(),
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,

    onDismissRequest: () -> Unit
) {
    CustomModalBottomSheet(
        modifier = modifier,
        isShow = isShow,
        sheetState = sheetState,
        sheetMaxWidth = sheetMaxWidth,
        onDismissRequest = onDismissRequest
    ) {
    }
}
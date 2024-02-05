package com.discut.manga.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.discut.manga.components.utils.maxHeightWithoutStatusBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomModalBottomSheet(
    modifier: Modifier = Modifier,
    isShow: Boolean = false,
    enableSpacer: Boolean = true,
    sheetState: SheetState = rememberModalBottomSheetState(),
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,

    onDismissRequest: () -> Unit = {},
    content: @Composable ColumnScope.(PaddingValues) -> Unit
) {
    if (isShow) {
        val bottomPadding =
            WindowInsets.navigationBars.asPaddingValues()
        ModalBottomSheet(
            modifier = Modifier
                //.requiredHeightIn(max = getMaxHeightWithoutStatusBar())

                //.maxHeightWithoutStatusBar()
                .statusBarSpacer(enableSpacer)
                .then(modifier),
            sheetState = sheetState,
            sheetMaxWidth = sheetMaxWidth,
            windowInsets = WindowInsets.captionBar,
            onDismissRequest = onDismissRequest,
        ) {
            Column(modifier = Modifier.padding(bottomPadding)) {
                content.invoke(this, bottomPadding)
            }
        }
    }
}

@Composable
private fun Modifier.statusBarSpacer(enableSpacer: Boolean): Modifier = if (enableSpacer) {
    maxHeightWithoutStatusBar()
} else {
    this
}
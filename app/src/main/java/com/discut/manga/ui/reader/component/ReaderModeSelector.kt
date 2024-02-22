package com.discut.manga.ui.reader.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.discut.manga.components.CustomSegmentedButtons
import com.discut.manga.preference.ReaderMode
import com.discut.manga.theme.padding

@Composable
fun ReaderModeSelector(
    modifier: Modifier = Modifier,
    onActiveItem: Any? = null,
    onActive: (key: Any) -> Unit,
){
    CustomSegmentedButtons(
        modifier = modifier,
        onActiveItem = onActiveItem,
        onActive = onActive
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
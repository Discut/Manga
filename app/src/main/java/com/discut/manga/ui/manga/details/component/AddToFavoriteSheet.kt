package com.discut.manga.ui.manga.details.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.discut.manga.components.CustomModalBottomSheet
import com.discut.manga.theme.padding
import discut.manga.data.category.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToFavoriteSheet(
    isShow: Boolean = false,
    categories: List<Category> = emptyList(),
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    var checkCategory by remember {
        mutableStateOf<Category?>(null)
    }
    CustomModalBottomSheet(
        isShow = isShow,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.padding.Normal)
                    .fillMaxWidth()
            ) {
                Text(text = "Add to favorite", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { /*TODO*/ }) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "add new favorite"
                        )
                        Text(text = "Cancel")
                    }
                }
            }
            categories.forEach {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.padding.Normal)
                        .fillMaxWidth()
                ) {
                    Text(text = it.name)
                    Checkbox(
                        checked = checkCategory?.name?.equals(it.name) ?: false,
                        onCheckedChange = { _ ->
                            checkCategory = it
                        }
                    )
                }
            }
        }
    }
}
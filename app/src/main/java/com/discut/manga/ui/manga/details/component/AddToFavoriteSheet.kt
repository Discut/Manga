package com.discut.manga.ui.manga.details.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.discut.manga.components.utils.maxHeightWithoutStatusBar
import com.discut.manga.theme.padding
import discut.manga.data.category.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToFavoriteSheet(
    categories: List<Category> = emptyList(),
    onAddClick: () -> Unit,
    onEditClick: () -> Unit,
    onConfirm: (Long) -> Unit,
    onDismissRequest: () -> Unit,
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    var checkCategory by remember {
        mutableStateOf<Category?>(null)
    }
    val onClickCategory: (Category) -> Unit = {
        checkCategory = if (checkCategory == it) {
            null
        } else {
            it
        }
    }
    val bottomPadding =
        WindowInsets.navigationBars.asPaddingValues()
    ModalBottomSheet(
        modifier = Modifier
            //.requiredHeightIn(max = getMaxHeightWithoutStatusBar())
            .maxHeightWithoutStatusBar(),
        sheetState = sheetState,
        sheetMaxWidth = sheetMaxWidth,
        windowInsets = WindowInsets.captionBar,
        onDismissRequest = {
            onDismissRequest()
            checkCategory = null
        },
    ) {
        Column(modifier = Modifier.padding(bottomPadding)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.padding.Normal)
                    .fillMaxWidth()
            ) {
                Text(text = "Add to favorite", style = MaterialTheme.typography.titleMedium)
                Row {
                    TextButton(onClick = onEditClick) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "favorite settings"
                            )
                            Text(text = "Edit")
                        }
                    }
                    TextButton(onClick = onAddClick) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = "add new favorite"
                            )
                            Text(text = "Add")
                        }
                    }
                }
            }
            categories.forEach {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onClickCategory(it) }
                        .padding(horizontal = MaterialTheme.padding.Normal)
                        .fillMaxWidth()
                ) {
                    Text(text = it.name)
                    Checkbox(
                        checked = checkCategory?.name?.equals(it.name) ?: false,
                        onCheckedChange = { _ ->
                            onClickCategory(it)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(MaterialTheme.padding.Large))
            FilledIconButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.padding.Normal),
                onClick = {
                    onConfirm(
                        checkCategory?.id ?: Category.UNCATEGORIZED_ID
                    )
                }
            ) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text(text = "Add to ")
                    Text(
                        text = checkCategory?.name ?: "Default",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
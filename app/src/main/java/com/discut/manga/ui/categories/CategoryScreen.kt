package com.discut.manga.ui.categories

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.discut.manga.components.preference.TextPreferenceComponent
import com.discut.manga.theme.padding
import discut.manga.data.category.Category
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import me.saket.swipe.rememberSwipeableActionsState
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navController: NavController,
    vm: CategoryViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var selectedEditCategory by remember { mutableStateOf<Category?>(null) }
    var selectedDeleteCategory by remember { mutableStateOf<Category?>(null) }
    var isShowAddDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Categories")
            },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { isShowAddDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add new category")
            }
        }
    ) { it ->

        val data by derivedStateOf {
            state.categories.map { c -> c.name }.toMutableStateList()
        }


        val reorderableState = rememberReorderableLazyListState(onMove = { from, to ->
/*            data.apply {
                add(to.index, removeAt(from.index))
            }*/
            vm.sendEvent(CategoryEvent.ItemMove(from.index, to.index))
        })

        DragListContent(
            modifier = Modifier.padding(it),
            state = reorderableState,
            list = data,
            onEdit = {
                selectedEditCategory = state.categories[it]
            },
            onDelete = {
                selectedDeleteCategory = state.categories[it]
            })

    }
    selectedEditCategory?.let {
        EditCategory(category = it,
            isFreeCategoryName = {
                !state.categories.any { c -> c.name == it }
            },
            onConfirm = { c ->
                vm.sendEvent(CategoryEvent.EditedCategory(c))
                selectedEditCategory = null
            }) {
            selectedEditCategory = null
        }
    }
    selectedDeleteCategory?.let { it ->
        DeleteCategory(
            category = it,
            onConfirm = {
                vm.sendEvent(CategoryEvent.DeleteCategory(it))
                selectedDeleteCategory = null
            }
        ) {
            selectedDeleteCategory = null
        }
    }
    if (isShowAddDialog) {
        NewCategory(
            isFreeCategoryName = {
                !state.categories.any { c -> c.name == it }
            },
            onConfirm = {
                vm.sendEvent(CategoryEvent.AddNewCategory(it))
                isShowAddDialog = false
            }) {
            isShowAddDialog = false
        }
    }
}

@Composable
fun DragListContent(
    modifier: Modifier = Modifier,
    state: ReorderableLazyListState,
    list: SnapshotStateList<String>,

    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    LazyColumn(
        state = state.listState,
        modifier = modifier
            .reorderable(state)
            .fillMaxHeight()
    ) {
        items(items = list, key = { it }) { item ->
            ReorderableItem(state, key = item) { isDragging ->
                val elevation = animateDpAsState(if (isDragging) 4.dp else 0.dp, label = "")
                val swipeableActionsState = rememberSwipeableActionsState()

                SwipeableActionsBox(
                    startActions = listOf(
                        SwipeAction(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    tint = (if (isSystemInDarkTheme()) {
                                        Color.Black
                                    } else {
                                        Color.White
                                    }),
                                    contentDescription = "Edit",
                                    modifier = Modifier.padding(MaterialTheme.padding.Normal)
                                )
                            },
                            background = MaterialTheme.colorScheme.primary,
                            onSwipe = {
                                onEdit(list.indexOf(item))
                            }

                        )
                    ),
                    endActions = listOf(
                        SwipeAction(
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    tint = (if (isSystemInDarkTheme()) {
                                        Color.Black
                                    } else {
                                        Color.White
                                    }),
                                    contentDescription = "Delete",
                                    modifier = Modifier.padding(MaterialTheme.padding.Normal)
                                )
                            },
                            background = Color.hsv(357.64f, .89f, .7843f),
                            onSwipe = {
                                onDelete(list.indexOf(item))
                            }

                        )
                    ),
                    state = swipeableActionsState,
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(elevation.value)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        TextPreferenceComponent(
                            onPreferenceClick = {},
                            title = item,
                            icon = Icons.AutoMirrored.Filled.Label,
                            endWidget = {
                                Icon(
                                    imageVector = Icons.Rounded.Menu, contentDescription = "Drag",
                                    modifier = Modifier.detectReorder(state)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun EditCategory(
    category: Category,
    isFreeCategoryName: (String) -> Boolean = { true },
    onConfirm: (Category) -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    var isShowOk by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(category.name) }
    AlertDialog(
        title = { Text(text = "Rename Category") },
        text = {
            OutlinedTextField(
                value = editText,
                label = { Text(text = "Name") },
                maxLines = 1,
                supportingText = { Text(text = "*Required") },
                onValueChange = {
                    isShowOk = it != category.name && it.isNotBlank() && isFreeCategoryName(it)
                    editText = it
                })
        },
        confirmButton = {
            TextButton(
                enabled = isShowOk,
                onClick = { onConfirm(category.copy(name = editText)) }) {
                Text(text = "Ok")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        },
        onDismissRequest = onDismissRequest,
    )
}

@Composable
internal fun NewCategory(
    isFreeCategoryName: (String) -> Boolean = { true },
    onConfirm: (String) -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    var isShowOk by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf("") }
    AlertDialog(
        title = { Text(text = "New Category") },
        text = {
            OutlinedTextField(
                value = editText,
                label = { Text(text = "Name") },
                maxLines = 1,
                supportingText = { Text(text = "*Required") },
                onValueChange = {
                    isShowOk = it.isNotBlank() && isFreeCategoryName(it)
                    editText = it
                })
        },
        confirmButton = {
            TextButton(
                enabled = isShowOk,
                onClick = { onConfirm(editText) }) {
                Text(text = "Ok")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        },
        onDismissRequest = onDismissRequest,
    )
}

@Composable
internal fun DeleteCategory(
    category: Category,
    onConfirm: (Category) -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    AlertDialog(
        title = { Text(text = "Delete Category") },
        text = { Text(text = "Are you sure you want to delete ${category.name}?") },
        confirmButton = {
            TextButton(onClick = { onConfirm(category) }) {
                Text(text = "Ok")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        },
        onDismissRequest = onDismissRequest,
    )
}

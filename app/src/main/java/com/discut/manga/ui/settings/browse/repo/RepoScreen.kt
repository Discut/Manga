package com.discut.manga.ui.settings.browse.repo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.discut.manga.components.preference.TextPreferenceComponent
import com.discut.manga.data.SnowFlakeUtil
import com.discut.manga.theme.padding
import discut.manga.data.source.SourceRepo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoScreen(
    vm: RepoViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val repos by state.repoStateFlow.collectAsStateWithLifecycle()
    var isShowAddDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Repo")
            },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { isShowAddDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add new repo")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
        ) {
            items(items = repos, key = { it.id }) { repo ->
                RepoItem(
                    repo = repo,
                    onDelete = { vm.sendEvent(RepoEvent.DeleteRepo(repo)) }
                )
            }
        }
    }

    if (isShowAddDialog) {
        NewRepo(
            isFreeRepo = { name, url ->
                repos.find { it.name == name || it.url == url } == null
            },
            onConfirm = { name, url ->
                vm.sendEvent(
                    RepoEvent.AddRepo(
                        SourceRepo.create().copy(
                            id = SnowFlakeUtil.generateSnowFlake(),
                            name = name,
                            url = url,
                            order = repos.size.toFloat() + 1
                        )
                    )
                )
                isShowAddDialog = false
            },
            onDismissRequest = { isShowAddDialog = false }
        )
    }

}

@Composable
private fun RepoItem(
    modifier: Modifier = Modifier,
    repo: SourceRepo,
    onDelete: () -> Unit
) {
    TextPreferenceComponent(
        title = repo.name,
        subTitle = repo.url,
        modifier = modifier,
        icon = Icons.AutoMirrored.Filled.Label,
        endWidget = {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete"
                )
            }
        }
    )
}

@Composable
internal fun NewRepo(
    isFreeRepo: (String, String) -> Boolean = { _, _ -> true },
    onConfirm: (String, String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var isShowOk by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editUrl by remember { mutableStateOf("") }
    AlertDialog(
        title = { Text(text = "New Repo") },
        text = {
            Column {
                OutlinedTextField(
                    value = editName,
                    label = { Text(text = "Name") },
                    maxLines = 1,
                    supportingText = { Text(text = "*Required") },
                    onValueChange = {
                        isShowOk = it.isNotBlank() && isFreeRepo(it, editUrl)
                        editName = it
                    })
                Spacer(modifier = Modifier.padding(top = MaterialTheme.padding.Normal))
                OutlinedTextField(
                    value = editUrl,
                    label = { Text(text = "Url") },
                    maxLines = 1,
                    supportingText = { Text(text = "*Required") },
                    onValueChange = {
                        isShowOk = it.isNotBlank() && isFreeRepo(editName, it)
                        editUrl = it
                    })
            }

        },
        confirmButton = {
            TextButton(
                enabled = isShowOk,
                onClick = { onConfirm(editName, editUrl) }) {
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

package com.discut.manga.ui.settings.download

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.discut.manga.components.preference.RadioPreferenceComponent
import com.discut.manga.components.preference.SwitchPreferenceComponent
import com.discut.manga.components.preference.rememberRadioPreferenceState
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadSettingsScreen(
    vm: DownloadSettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by vm.uiState.collectAsState()
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Download",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
    ) {
        Content(
            modifier = Modifier
                .padding(it)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            wifiOnly = state.wifiOnly.collectAsStateWithLifecycle().value,
            downloadDirs = state.downloadDirMap,
            downloadDefaultDirKey = state.downloadDirDefault,
            downloadInterval = state.downloadInterval,
            onWifiOnlyPreferenceClick = { value ->
                vm.sendEvent(DownloadSettingsEvent.WifiOnlyChanged(value))
            },
            onDownloadIntervalChanged = { value ->
                vm.sendEvent(DownloadSettingsEvent.DownloadIntervalChanged(value))
            }
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    wifiOnly: Boolean = true,

    downloadDirs: Map<String, String>,
    downloadDefaultDirKey: String? = null,
    downloadInterval: Int = 0,

    onWifiOnlyPreferenceClick: (Boolean) -> Unit,
    onDownloadIntervalChanged: (Int) -> Unit
) {
    val context = LocalContext.current
    var selectedDir by remember { mutableStateOf(downloadDirs[downloadDefaultDirKey]) }
    val launcher: ActivityResultLauncher<Intent> = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedDir = getRealPathFromURI(context = context, uri = data?.data!!)
        }
    }
    val downloadDirState = rememberRadioPreferenceState(data = downloadDirs,
        default = downloadDefaultDirKey,
        onSelected = { k, _ ->
            if (k == "custom") {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                launcher.launch(intent)
            } else {
                selectedDir = downloadDirs[k] ?: ""
            }
        })
    val downloadIntervalState = rememberRadioPreferenceState(
        default = downloadInterval,
        data = mapOf(
            0 to "0",
            100 to "100",
            200 to "200",
            300 to "300",
        ), onSelected = { key, _ ->
            onDownloadIntervalChanged(key)
        }
    )
    Column(modifier = modifier) {
        RadioPreferenceComponent(
            title = "Download dir",
            subTitle = selectedDir, state = downloadDirState
        )
        SwitchPreferenceComponent(
            title = "Only wifi",
            subTitle = "Only download when connected to wifi", state = wifiOnly
        ) { _, n ->
            onWifiOnlyPreferenceClick(n)
        }
        RadioPreferenceComponent(
            title = "Download interval",
            subTitle = downloadInterval.toString(), state = downloadIntervalState
        )
    }

}

fun getRealPathFromURI(context: Context, uri: Uri): String {
    val docUri = DocumentsContract.buildDocumentUriUsingTree(
        uri,
        DocumentsContract.getTreeDocumentId(uri)
    )
    val docCursor = context.contentResolver.query(docUri, null, null, null, null)
    var str: String = ""
    while (docCursor!!.moveToNext()) {
        str = docCursor.getString(0)
        if (str.matches(Regex(".*:.*"))) break
    }
    docCursor.close()
    val split = str.split(":")
    val base: File =
        if (split[0] == "primary") Environment.getExternalStorageDirectory()
        else File("/storage/${split[0]}")

    if (!base.isDirectory) throw Exception("'${uri}' cannot be resolved in a valid path")
    return File(base, split[1]).canonicalPath.toString()
}

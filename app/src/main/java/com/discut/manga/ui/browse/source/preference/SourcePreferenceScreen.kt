package com.discut.manga.ui.browse.source.preference

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.discut.manga.preference.sourcePreferences
import com.discut.manga.service.source.SourceManager
import manga.source.ConfigurationSource
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.getPreferenceFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourcePreferenceScreen(
    sourceId: Long,
    onBack: () -> Unit,
) {
    val sourceManager = SourceManager.instance
    val source = sourceManager.get(sourceId) ?: return
    if (source !is ConfigurationSource) {
        return
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = source.name)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        ProvidePreferenceLocals(
            flow = source.getPreferences().getPreferenceFlow()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                sourcePreferences(source)
            }
        }
    }
}


package com.discut.manga.ui.settings.download

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.lifecycle.viewModelScope
import com.discut.core.mvi.BaseViewModel
import com.discut.manga.App
import com.discut.manga.util.get
import com.discut.manga.util.launchIO
import com.discut.manga.util.launchUI
import com.discut.manga.util.withIOContext
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import manga.core.preference.DownloadPreference
import manga.core.preference.PreferenceManager
import javax.inject.Inject


@HiltViewModel
class DownloadSettingsViewModel @Inject constructor(
) :
    BaseViewModel<DownloadSettingsState, DownloadSettingsEvent, DownloadSettingsEffect>() {

    private val downloadPreference = PreferenceManager.get<DownloadPreference>()
    override fun initialState(): DownloadSettingsState = DownloadSettingsState(
        downloadInterval = MutableStateFlow(downloadPreference.getDownloadInterval()),
        wifiOnly = MutableStateFlow(downloadPreference.getIsWifiOnly())
    )

    init {
        sendEvent(DownloadSettingsEvent.Init)

        viewModelScope.launchIO {
            val wifiOnlyFlow = downloadPreference.getIsWifiOnlyAsFlow().distinctUntilChanged()
                .stateIn(CoroutineScope(Dispatchers.IO))
            val downloadInterval =
                downloadPreference.getDownloadIntervalAsFlow().distinctUntilChanged()
                    .stateIn(CoroutineScope(Dispatchers.IO))
            sendState {
                copy(
                    wifiOnly = wifiOnlyFlow,
                    downloadInterval = downloadInterval
                )
            }
        }
    }

    override suspend fun handleEvent(
        event: DownloadSettingsEvent,
        state: DownloadSettingsState
    ): DownloadSettingsState =
        when (event) {
            is DownloadSettingsEvent.Init -> {
                val defaultDir = downloadPreference.getDefaultDownloadDirectory(App.instance)
                val currentDir = downloadPreference.getDownloadDirectory(App.instance)
                state.copy(
                    downloadDirMap = mapOf(
                        "default" to defaultDir,
                        "custom" to if (currentDir == defaultDir) {
                            "Custom location"
                        } else {
                            currentDir
                        }
                    ),
                    downloadDirDefault = if (defaultDir == currentDir
                    ) {
                        "default"
                    } else {
                        "custom"
                    },
                )
            }

            is DownloadSettingsEvent.DownloadDirChanged -> {
                downloadPreference.setDownloadDirectory(event.dir)
                state
            }

            is DownloadSettingsEvent.DownloadIntervalChanged -> {
                downloadPreference.setDownloadInterval(
                    event.interval
                )
                state
            }

            is DownloadSettingsEvent.WifiOnlyChanged -> {
                downloadPreference.setWifiOnly(event.wifiOnly)
                state
            }
        }

}
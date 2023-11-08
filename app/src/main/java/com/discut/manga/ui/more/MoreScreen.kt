package com.discut.manga.ui.more

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.discut.core.mvi.CollectSideEffect
import com.discut.manga.components.preference.SwitchPreferenceComponent
import com.discut.manga.ui.base.BaseScreen
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoreScreen @Inject constructor() : BaseScreen<MoreScreenViewModel>() {
    @Composable
    override fun Content(viewModel: MoreScreenViewModel) {
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        SideEffect {
            Log.d("MoreScreen", state.toString())

        }

        viewModel.CollectSideEffect {
            when (it) {
                is MoreScreenEffect.SecurityModeChange -> {
                    //state.enableSecurityMode = it.enable
                }
            }
        }
        LazyColumn {
            item {
                SwitchPreferenceComponent(
                    title = "隐私模式", subTitle = "隐私模式",
                    //icon = Icons.Default.Security,
                    state = state.enableSecurityMode,
                    onSwitchClick = {
                        viewModel.sendEvent(
                            MoreScreenEvent.ClickSecurityMode(
                                !state.enableSecurityMode
                            )
                        )
                    }
                ) { old, new ->

                }
            }
        }
    }

    @Composable
    override fun getViewModel(): MoreScreenViewModel {
        return viewModel()
    }

    override fun getRoute(): String = "/more"
}
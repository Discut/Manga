package com.discut.manga.ui.main.domain

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.discut.manga.ui.base.BaseScreen

data class NavBarItem(val title: String, val route: String, val icon: ImageVector, val screen: BaseScreen<out ViewModel>)
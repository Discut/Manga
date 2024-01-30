package com.discut.manga.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.discut.core.flowbus.observeEvent
import com.discut.manga.event.NavigationEvent
import com.discut.manga.navigation.NavigationRoute
import com.discut.manga.navigation.composableWithAnimation
import com.discut.manga.navigation.settingsGraph
import com.discut.manga.ui.base.BaseActivity
import com.discut.manga.ui.browse.viewer.MangasViewer
import com.discut.manga.ui.categories.CategoryScreen
import com.discut.manga.ui.download.DownloadScreen
import com.discut.manga.ui.main.domain.ToRouteEvent
import com.discut.manga.ui.manga.details.MangaDetailsScreen
import com.discut.manga.util.setComposeContent
import com.discut.manga.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerExtensionChangeEventReceiver(this)
        checkStorageManagerPermission()
        setComposeContent {
            val navController = rememberNavController()
            LocalLifecycleOwner.current.observeEvent<ToRouteEvent> {
                navController.navigate(it.route) {
                    if (!it.popup) return@navigate
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            LocalLifecycleOwner.current.observeEvent<NavigationEvent> {
                navController.navigate(it.route)
            }
            NavHost(
                navController = navController,
                startDestination = NavigationRoute.MainScreen.route
            ) {
                composableWithAnimation(NavigationRoute.MainScreen.route) {
                    MainScreen()
                }
                composableWithAnimation(
                    route = NavigationRoute.MangasViewerScreen.route,
                    arguments = NavigationRoute.MangasViewerScreen.arguments
                ) {
                    val sourceId = it.arguments?.getLong("sourceId")
                        ?: throw IllegalArgumentException("sourceId is null")
                    val queryKey = it.arguments?.getString("queryKey")
                        ?: ""
                    MangasViewer(sourceId = sourceId, queryKey = queryKey) {
                        navController.popBackStack()
                    }
                }
                composable(
                    route = NavigationRoute.MangaDetailsScreen.route,
                    arguments = NavigationRoute.MangaDetailsScreen.arguments
                ) {
                    val mangaId = it.arguments?.getLong("mangaId")
                        ?: throw IllegalArgumentException("mangaId is null")
                    MangaDetailsScreen(mangaId = mangaId) {
                        navController.popBackStack()
                    }

                }
                composableWithAnimation(NavigationRoute.CategoryScreen.route) {
                    CategoryScreen(navController)
                }
                composableWithAnimation(NavigationRoute.DownloadScreen.route) {
                    DownloadScreen {
                        navController.popBackStack()
                    }
                }
                settingsGraph(navController)
            }
        }
    }

    private fun checkStorageManagerPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R ||
            Environment.isExternalStorageManager()
        ) {
            toast("已获得访问所有文件权限")
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            startActivity(intent)
        }
    }

}
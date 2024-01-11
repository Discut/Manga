package com.discut.manga.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.discut.manga.ui.settings.SettingsScreen
import com.discut.manga.ui.settings.security.SecuritySettingsScreen

sealed class NavigationRoute(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    data object MainScreen : NavigationRoute("main")

    data object MangaDetailsScreen : NavigationRoute(
        route = "mangaDetails/{mangaId}",
        arguments = listOf(
            navArgument("mangaId") { type = NavType.LongType }
        ))

    data object MangasViewerScreen : NavigationRoute(
        route = "mangasViewer/{sourceId}?queryKey={queryKey}",
        arguments = listOf(
            navArgument("sourceId") { type = NavType.LongType },
            navArgument("queryKey") {
                type = NavType.StringType
                defaultValue = ""
            },
        )
    )

    data object CategoryScreen : NavigationRoute("category")

    data object DownloadScreen : NavigationRoute("download")

    data object SettingsScreen : NavigationRoute("settings") {
        data object Security : NavigationRoute("security")
        data object SettingsMain : NavigationRoute("settingsMain")
    }
}

fun NavGraphBuilder.settingsGraph(navController: NavController) {
    navigation(
        startDestination = NavigationRoute.SettingsScreen.SettingsMain.route,
        route = NavigationRoute.SettingsScreen.route
    ) {
        composableWithAnimation(route = NavigationRoute.SettingsScreen.SettingsMain.route) {
            SettingsScreen(navController)
        }
        composableWithAnimation(route = NavigationRoute.SettingsScreen.Security.route) {
            SecuritySettingsScreen {
                navController.popBackStack()
            }
        }
    }
}

/**
 * 动画文章 https://juejin.cn/post/7046855025758306317#heading-12
 */
fun scaleIntoContainer(
    direction: ScaleTransitionDirection = ScaleTransitionDirection.INWARDS,
    initialScale: Float = if (direction != ScaleTransitionDirection.OUTWARDS) 0.95f else 1.05f
): EnterTransition {
    return scaleIn(
        animationSpec = tween(180, delayMillis = 90),
        initialScale = initialScale
    ) + fadeIn(animationSpec = tween(180, delayMillis = 90))
}


enum class ScaleTransitionDirection {
    OUTWARDS, INWARDS
}

fun scaleOutOfContainer(
    direction: ScaleTransitionDirection = ScaleTransitionDirection.OUTWARDS,
    targetScale: Float = if (direction != ScaleTransitionDirection.INWARDS) 0.95f else 1.05f
): ExitTransition {
    return scaleOut(
        animationSpec = tween(
            durationMillis = 180,
            delayMillis = 90
        ), targetScale = targetScale
    ) + fadeOut(tween(delayMillis = 90))
}

internal fun NavGraphBuilder.composableWithAnimation(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = {
        slideInHorizontally(initialOffsetX = { it / 10 }) + fadeIn()
    },
    exitTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null/*{
        slideOutHorizontally(targetOffsetX = { it / 8 }) + fadeOut()
    }*/,
    popEnterTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        {
            fadeIn(initialAlpha = .5f)
        },
    popExitTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        {
            slideOutHorizontally(targetOffsetX = { it / 10 }) + fadeOut()
        },
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        content = content
    )
}
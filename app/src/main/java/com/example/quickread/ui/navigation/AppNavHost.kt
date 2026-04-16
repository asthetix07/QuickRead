package com.example.quickread.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.quickread.viewmodel.NewsViewModel
import com.example.quickread.ui.screens.HomeScreen
import com.example.quickread.ui.screens.SavedNewsScreen
import com.example.quickread.ui.screens.SearchNewsScreen
import com.example.quickread.ui.screens.TopNewsScreen
import com.example.quickread.ui.screens.WebViewScreen

/**
 * App-level navigation host defining all screen destinations.
 *
 * @param navController  The app's [NavHostController].
 * @param deepLinkUrl    Optional article URL from a notification deep link.
 * @param onDeepLinkConsumed Callback invoked after the deep link has been navigated to,
 *                           preventing duplicate navigations on recomposition.
 * @param modifier       Optional [Modifier] for the host container.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    deepLinkUrl: String? = null,
    onDeepLinkConsumed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val viewModel: NewsViewModel = hiltViewModel()

    // ── Deep link navigation ─────────────────────────────────────────
    LaunchedEffect(deepLinkUrl) {
        if (!deepLinkUrl.isNullOrBlank()) {
            val encodedUrl = Uri.encode(deepLinkUrl)
            navController.navigate("webView/$encodedUrl") {
                // Ensure "home" is at the bottom of the back stack
                popUpTo("home") { inclusive = false }
                launchSingleTop = true
            }
            onDeepLinkConsumed()
        }
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                modifier = modifier,
                navController = navController
            )
        }

        composable("one") {
            TopNewsScreen(
                modifier = modifier,
                navController = navController,
                viewModel = viewModel,
            )
        }

        composable("two") {
            SavedNewsScreen(
                modifier = modifier,
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("three") {
            SearchNewsScreen(
                modifier = modifier,
                navController = navController,
                viewModel = viewModel,
            )
        }

        composable(
            "webView/{url}",
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url") ?: ""
            WebViewScreen(
                url = Uri.decode(url),
                onBack = {
                    // Go back to the previous screen (latest news, saved, search, etc.)
                    navController.popBackStack()
                }
            )
        }
    }
}


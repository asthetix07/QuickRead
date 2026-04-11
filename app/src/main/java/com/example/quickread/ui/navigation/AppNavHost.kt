package com.example.quickread.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.quickread.models.NewsViewModel
import com.example.quickread.ui.screens.HomeScreen
import com.example.quickread.ui.screens.SavedNewsScreen
import com.example.quickread.ui.screens.SearchNewsScreen
import com.example.quickread.ui.screens.TopNewsScreen
import com.example.quickread.ui.screens.WebViewScreen

/**
 * App-level navigation host defining all screen destinations.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val viewModel: NewsViewModel = hiltViewModel()

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
                onBack = { navController.popBackStack() }
            )
        }
    }
}

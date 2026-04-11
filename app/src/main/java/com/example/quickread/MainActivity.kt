package com.example.quickread

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.quickread.notification.NotificationScheduler
import com.example.quickread.ui.components.BottomNavItem
import com.example.quickread.ui.components.QuickReadBottomBar
import com.example.quickread.ui.components.QuickReadTopBar
import com.example.quickread.ui.navigation.AppNavHost
import com.example.quickread.ui.theme.QuickReadTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity host for the Compose-based QuickRead UI.
 *
 * Responsibilities:
 * - Requests POST_NOTIFICATIONS permission on Android 13+.
 * - Schedules the periodic news notification worker after permission is granted.
 * - Hosts the navigation graph, top bar, and bottom bar.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickReadTheme {
                val context = LocalContext.current

                // ── Notification permission + worker scheduling ──────────

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { granted ->
                    if (granted) NotificationScheduler.schedule(context)
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val status = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.POST_NOTIFICATIONS
                        )
                        if (status == PackageManager.PERMISSION_GRANTED) {
                            NotificationScheduler.schedule(context)
                        } else {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        NotificationScheduler.schedule(context)
                    }
                }

                // ── Navigation ───────────────────────────────────────────

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                BackHandler {
                    if (currentRoute != "home") {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        finish()
                    }
                }

                val bottomNavItems = listOf(
                    BottomNavItem("one", Icons.Default.Info, "Trending"),
                    BottomNavItem("two", Icons.Default.Star, "Saved"),
                    BottomNavItem("three", Icons.Default.Search, "Search")
                )

                val isWebView = currentRoute?.startsWith("webView") == true

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        if (!isWebView) QuickReadTopBar(currentRoute = currentRoute)
                    },
                    bottomBar = {
                        if (!isWebView) {
                            QuickReadBottomBar(
                                items = bottomNavItems,
                                currentRoute = currentRoute,
                                onItemClick = { route ->
                                    navController.navigate(route) {
                                        popUpTo("home") { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(
                            top = innerPadding.calculateTopPadding(),
                            bottom = 0.dp
                        )
                    )
                }
            }
        }
    }
}

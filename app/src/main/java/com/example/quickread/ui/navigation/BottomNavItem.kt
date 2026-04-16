package com.example.quickread.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents a single item in the bottom navigation bar.
 *
 * @param route The navigation route this item maps to.
 * @param icon The vector icon displayed in the bar.
 * @param label The user-facing label displayed beneath the icon.
 */
data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

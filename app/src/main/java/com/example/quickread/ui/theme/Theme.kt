package com.example.quickread.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Dark Color Scheme ────────────────────────────────────────────────
private val QuickReadDarkScheme = darkColorScheme(
    primary = DarkBlue,
    onPrimary = LightBeige,
    primaryContainer = MediumBlue,
    onPrimaryContainer = LightGray,

    secondary = MediumBlue,
    onSecondary = LightBeige,
    secondaryContainer = DarkBlue,
    onSecondaryContainer = LightBlueGray,

    tertiary = TanBrown,
    onTertiary = DarkBlue,
    tertiaryContainer = TanBrown,
    onTertiaryContainer = LightBeige,

    background = DarkBlue,
    onBackground = LightGray,

    surface = Color(0xFF0D2147),       // slightly lighter than DarkBlue for cards
    onSurface = LightGray,
    surfaceVariant = MediumBlue,
    onSurfaceVariant = LightBlueGray,

    outline = LightBlueGray,
    outlineVariant = MediumBlue,

    error = Color(0xFFCF6679),
    onError = Color.Black,

    inverseSurface = LightGray,
    inverseOnSurface = DarkBlue,
    inversePrimary = MediumBlue,
)

// ── Light Color Scheme ───────────────────────────────────────────────
private val QuickReadLightScheme = lightColorScheme(
    primary = DarkBlue,
    onPrimary = LightBeige,
    primaryContainer = LightBlueGray,
    onPrimaryContainer = DarkBlue,

    secondary = MediumBlue,
    onSecondary = Color.White,
    secondaryContainer = LightGray,
    onSecondaryContainer = DarkBlue,

    tertiary = TanBrown,
    onTertiary = Color.White,
    tertiaryContainer = LightBeige,
    onTertiaryContainer = DarkBlue,

    background = LightGray,
    onBackground = DarkBlue,

    surface = Color.White,
    onSurface = DarkBlue,
    surfaceVariant = LightGray,
    onSurfaceVariant = MediumBlue,

    outline = MediumBlue,
    outlineVariant = LightBlueGray,

    error = Color(0xFFB3261E),
    onError = Color.White,

    inverseSurface = DarkBlue,
    inverseOnSurface = LightGray,
    inversePrimary = LightBlueGray,
)

// ── Theme ────────────────────────────────────────────────────────────
@Suppress("DEPRECATION")
@Composable
fun QuickReadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) QuickReadDarkScheme else QuickReadLightScheme

    // Tint the system status / navigation bars to match the active theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DarkBlue.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
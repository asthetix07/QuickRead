package com.example.quickread.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickread.ui.theme.DarkBlue
import com.example.quickread.ui.theme.MediumBlue
import java.util.Calendar

/**
 * Reusable top app bar with gradient background.
 * Displays a personalized, time-based greeting on the home screen,
 * and contextual titles on other screens.
 *
 * @param currentRoute The current navigation route, used to determine the title.
 * @param modifier Optional [Modifier] for the root container.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickReadTopBar(
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    val title = remember(currentRoute) {
        getTopBarTitle(currentRoute)
    }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DarkBlue, MediumBlue),
                        startY = 25f,
                        endY = 200f
                    )
                )
        )
        TopAppBar(
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color.White,
                    fontSize = 22.sp
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}

/**
 * Returns the appropriate top bar title based on the current route.
 * For the home screen, it generates a time-aware personalized greeting.
 */
private fun getTopBarTitle(route: String?): String {
    return when (route) {
        "home", null -> getTimeBasedGreeting()
        "one" -> "Latest News 📰"
        "two" -> "Saved Articles ⭐"
        "three" -> "Search News 🔍"
        else -> "Quick Read"
    }
}

/**
 * Generates a greeting string based on the current hour of the day.
 */
private fun getTimeBasedGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Hii, good morning \uD83C\uDF04"
        in 12..16 -> "Hii, good afternoon ☀\uFE0F"
        in 17..20 -> "Hii, good evening \uD83C\uDF06"
        else -> "Hii, good night \uD83C\uDF03"
    }
}

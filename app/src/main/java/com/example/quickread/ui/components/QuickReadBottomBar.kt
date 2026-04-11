package com.example.quickread.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.quickread.ui.theme.LightBlueGray
import com.example.quickread.ui.theme.TanBrown

/**
 * Represents a single item in the bottom navigation bar.
 */
data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

/**
 * Reusable floating pill-shaped bottom navigation bar.
 *
 * @param items The list of navigation items to display.
 * @param currentRoute The currently active route (used to highlight the selected item).
 * @param onItemClick Callback invoked when a navigation item is tapped.
 * @param modifier Optional [Modifier] for the root container.
 */
@Composable
fun QuickReadBottomBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        containerColor = Color.Transparent,
        modifier = modifier
            .padding(horizontal = 32.dp, vertical = 16.dp)
            .navigationBarsPadding()
            .shadow(24.dp, RoundedCornerShape(45.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2C3140), // Subtle reflection highlight
                        Color(0xFF0F121B)  // Deep base
                    )
                ),
                shape = RoundedCornerShape(45.dp)
            )
            .border(0.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(45.dp))
            .clip(RoundedCornerShape(45.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                TextButton(
                    onClick = { if (!isSelected) onItemClick(item.route) }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) TanBrown else LightBlueGray
                        )
                        Text(
                            text = item.label,
                            color = if (isSelected) TanBrown else LightBlueGray,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

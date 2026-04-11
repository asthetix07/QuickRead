package com.example.quickread.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickread.ui.theme.TanBrown

/**
 * Data class representing a news category chip.
 *
 * @param displayName The user-facing label shown on the chip.
 * @param apiValue The lowercase value sent to the NewsAPI `category` query parameter,
 *                 or null for the "All" chip (no category filter).
 */
private data class Category(
    val displayName: String,
    val apiValue: String?
)

/**
 * Horizontal scrollable row of capsule-shaped category chips.
 *
 * Selecting a chip triggers [onCategorySelected] with the corresponding
 * API category value (or null for "All"). The selected chip is highlighted
 * with the app accent colour; unselected chips use an outlined style.
 *
 * @param selectedCategory The currently active category API value (null = All).
 * @param onCategorySelected Callback fired when the user taps a chip.
 * @param modifier Optional [Modifier] for the root container.
 */
@Composable
fun CategoryChipBar(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = remember {
        listOf(
            Category("All", null),
            Category("Health", "health"),
            Category("Science", "science"),
            Category("Sports", "sports"),
            Category("Technology", "technology"),
            Category("Business", "business"),
            Category("Entertainment", "entertainment")
        )
    }

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories, key = { it.displayName }) { category ->
            CategoryChip(
                label = category.displayName,
                isSelected = selectedCategory == category.apiValue,
                onClick = { onCategorySelected(category.apiValue) }
            )
        }
    }
}

/**
 * Individual capsule-shaped chip with animated colour transitions.
 */
@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) TanBrown else Color.Transparent,
        animationSpec = tween(durationMillis = 250),
        label = "chipBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 250),
        label = "chipText"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) TanBrown else MaterialTheme.colorScheme.outline,
        animationSpec = tween(durationMillis = 250),
        label = "chipBorder"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Text(
            text = label,
            color = contentColor,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

package com.example.quickread.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quickread.ui.theme.TanBrown

/**
 * Reusable full-screen centered loading indicator with a semi-transparent
 * overlay, animated spinner, and "Loading…" label.
 *
 * Works correctly in both light and dark themes by using MaterialTheme
 * tokens for the overlay background.
 *
 * @param modifier Optional [Modifier] for the root container.
 * @param showLabel Whether to display the "Loading…" label beneath the spinner.
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinner_rotation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .rotate(rotation),
                color = TanBrown,
                strokeWidth = 4.dp,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            if (showLabel) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading…",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

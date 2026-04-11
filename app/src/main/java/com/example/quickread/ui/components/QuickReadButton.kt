package com.example.quickread.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quickread.ui.theme.DarkBlue
import com.example.quickread.ui.theme.LightBeige
import com.example.quickread.ui.theme.TanBrown

/**
 * Reusable styled button used across the app.
 *
 * Theme-aware: uses a visible accent container in dark mode so buttons
 * are clearly distinguishable against the dark background.
 *
 * @param text The button label.
 * @param onClick Callback invoked on click.
 * @param modifier Optional [Modifier].
 * @param fillWidth Whether the button should take full width (default: false).
 */
@Composable
fun QuickReadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fillWidth: Boolean = false
) {
    val isDark = isSystemInDarkTheme()

    val containerColor = if (isDark) TanBrown else DarkBlue
    val contentColor = if (isDark) DarkBlue else LightBeige
    val borderColor = if (isDark) TanBrown.copy(alpha = 0.6f) else DarkBlue.copy(alpha = 0.3f)

    Button(
        onClick = onClick,
        modifier = modifier
            .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
            .height(48.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = BorderStroke(1.dp, borderColor),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

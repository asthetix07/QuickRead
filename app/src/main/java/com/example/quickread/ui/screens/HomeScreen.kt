package com.example.quickread.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.quickread.ui.components.QuickReadButton
import com.example.quickread.ui.theme.TanBrown

@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "What's happening? \nTo get started, click below 👇",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        QuickReadButton(
            text = "Trending News",
            onClick = { navController.navigate("one") },
            fillWidth = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        QuickReadButton(
            text = "Saved News",
            onClick = { navController.navigate("two") },
            fillWidth = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        QuickReadButton(
            text = "Search News",
            onClick = { navController.navigate("three") },
            fillWidth = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(
                    text = "Created by",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "an amazing team 🚀",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { shareApp(context) }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share App",
                    tint = TanBrown
                )
            }
        }
    }
}

private fun shareApp(context: Context) {
    try {
        val shareText = "Made this news app for real-time, clutter-free reading 📰\n" +
                "If you love news, tech, or politics, give it a try 🙌"
        val driveLink = "https://drive.google.com/drive/folders/1LV9wthJ7Phozvj-zxhg0PL-TiqLflfEe?usp=sharing"
        val fullMessage = "$shareText\n\n$driveLink"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, fullMessage)
        }

        context.startActivity(Intent.createChooser(intent, "Share App via"))
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to share link.", Toast.LENGTH_SHORT).show()
    }
}

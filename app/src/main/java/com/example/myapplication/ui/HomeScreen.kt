package com.example.myapplication.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.example.myapplication.R
import java.io.File

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
            text = "What's happening around the 🌐\nTo get started, click below 👇",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate("one") },
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.p2))
        ) {
            Text("Top News", color = colorResource(id = R.color.p4))
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            onClick = { navController.navigate("two") },
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.p2))
        ) {
            Text("Saved News", color = colorResource(id = R.color.p4))
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            onClick = { navController.navigate("three") },
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.p2))
        ) {
            Text("Search News", color = colorResource(id = R.color.p4))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Created by AKASH",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            IconButton(onClick = {
                Log.d("ShareApp", "Button clicked")
                shareApp(context)
            }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share App"
                )
            }


        }
    }
}


fun shareApp(context: Context) {
    try {
        Log.d("ShareApp", "Entered shareApp()")

        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val sourceApk = packageInfo.applicationInfo?.sourceDir?.let { File(it) }
        if (sourceApk != null) {
            Log.d("ShareApp", "APK File: ${sourceApk.path}")
        }

        // Copy APK to external cache directory
        val cachePath = File(context.externalCacheDir, "shared_apk")
        cachePath.mkdirs()
        val outFile = File(cachePath, "QuickRead.apk")

        sourceApk?.copyTo(outFile, overwrite = true)

        val apkUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            outFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.android.package-archive"
            putExtra(Intent.EXTRA_STREAM, apkUri)
            putExtra(Intent.EXTRA_TEXT, "Made this news app for real-time, clutter-free reading 📰\n" + "If you love news, tech, or politics, give it a try 🙌")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Share App via"))

    } catch (e: Exception) {
        Log.e("ShareApp", "Error sharing app: ${e.message}", e)
        Toast.makeText(context, "Failed to share app.", Toast.LENGTH_SHORT).show()
    }
}




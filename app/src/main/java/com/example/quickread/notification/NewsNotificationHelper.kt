package com.example.quickread.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.quickread.MainActivity
import com.example.quickread.R
import java.net.HttpURLConnection
import java.net.URL

/**
 * Builds and posts collapsed BigPicture-style news notifications.
 *
 * - Downloads the article image from the network on the calling (worker) thread.
 * - Falls back to [R.drawable.no_img] when the URL is absent or download fails.
 * - Creates the notification channel lazily on Android O+.
 */
object NewsNotificationHelper {

    private const val CHANNEL_ID = "news_channel"
    private const val CHANNEL_NAME = "News Updates"
    private const val CHANNEL_DESC = "Periodic top-headline news notifications"
    private const val NOTIFICATION_ID = 1001

    /**
     * Creates the notification channel. Safe to call repeatedly —
     * the system ignores duplicate channel creation.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = CHANNEL_DESC }

            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    /**
     * Posts a collapsed notification with a BigPicture expansion style.
     *
     * @param context     Application context.
     * @param title       Article headline.
     * @param description Article summary.
     * @param imageUrl    Image URL; `null` triggers the fallback drawable.
     */
    fun showNewsNotification(
        context: Context,
        title: String,
        description: String,
        imageUrl: String?
    ) {
        createNotificationChannel(context)

        val bitmap = downloadBitmap(imageUrl) ?: getFallbackBitmap(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setLargeIcon(bitmap)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .bigLargeIcon(null as Bitmap?)
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        context.getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, notification)
    }

    /**
     * Blocking image download — called only from a WorkManager background thread.
     *
     * @return the decoded bitmap, or `null` on any failure.
     */
    private fun downloadBitmap(url: String?): Bitmap? {
        if (url.isNullOrBlank()) return null
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000
            connection.doInput = true
            connection.connect()
            connection.inputStream.use { BitmapFactory.decodeStream(it) }
                .also { connection.disconnect() }
        } catch (_: Exception) {
            null
        }
    }

    private fun getFallbackBitmap(context: Context): Bitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.no_img)
}

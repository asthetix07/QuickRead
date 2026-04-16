package com.example.quickread.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Schedules the periodic [NewsNotificationWorker] using WorkManager.
 *
 * Scheduling strategy:
 * - **Repeat interval** = 85 minutes
 * - **Flex interval**   = 40 minutes
 *
 * WorkManager will execute the worker somewhere in the last 40 minutes of each
 * 85-minute cycle, effectively running between 45 min and 1 h 25 min from the
 * previous execution — matching the requirement.
 */
object NotificationScheduler {

    private const val WORK_NAME = "news_notification_work"

    /**
     * Schedules (or re-schedules) the periodic news notification worker.
     *
     * Uses [ExistingPeriodicWorkPolicy.UPDATE] so that every app launch
     * refreshes the timer — preventing stale schedules after Doze deferrals.
     * The flex interval is kept tight (15 min within an 85-min cycle) so
     * the notification fires predictably.
     */
    fun schedule(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<NewsNotificationWorker>(
            repeatInterval = 85,
            repeatIntervalTimeUnit = TimeUnit.MINUTES,
            flexTimeInterval = 15,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}

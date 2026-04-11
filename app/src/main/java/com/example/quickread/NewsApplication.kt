package com.example.quickread

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.quickread.notification.NewsNotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application entry point. Configures Hilt DI, initialises the notification
 * channel, and provides a custom WorkManager configuration backed by
 * [HiltWorkerFactory] for `@HiltWorker`-annotated workers.
 */
@HiltAndroidApp
class NewsApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        NewsNotificationHelper.createNotificationChannel(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
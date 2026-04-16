package com.example.quickread.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.quickread.api.NewsAPI
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Background worker that fetches top headlines and shows a random article
 * as a collapsed BigPicture notification.
 *
 * Uses Hilt-assisted injection to receive [NewsAPI] while WorkManager
 * provides [Context] and [WorkerParameters].
 */
@HiltWorker
class NewsNotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val newsApi: NewsAPI
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val response = newsApi.getTopHeadlines()
            if (!response.isSuccessful) return Result.retry()

            val articles = response.body()?.articles.orEmpty()
            if (articles.isNotEmpty()) {
                val article = articles.random()
                NewsNotificationHelper.showNewsNotification(
                    context = applicationContext,
                    title = article.title ?: "QuickRead News",
                    description = article.description ?: "Tap to read the latest news",
                    imageUrl = article.urlToImage,
                    articleUrl = article.url
                )
            }
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}

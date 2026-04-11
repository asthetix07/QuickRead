package com.example.quickread.repository

import com.example.quickread.api.NewsAPI
import com.example.quickread.models.NewsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

/**
 * Production implementation of [NewsRepository].
 * All network calls are dispatched to [Dispatchers.IO].
 */
class NewsRepositoryImpl @Inject constructor(
    private val api: NewsAPI
) : NewsRepository {

    override suspend fun getTopHeadlines(
        country: String, page: Int, category: String?
    ): Response<NewsResponse> = withContext(Dispatchers.IO) {
        api.getTopHeadlines(country, page, category)
    }

    override suspend fun searchNews(
        query: String, page: Int
    ): Response<NewsResponse> = withContext(Dispatchers.IO) {
        api.searchNews(query, page)
    }
}
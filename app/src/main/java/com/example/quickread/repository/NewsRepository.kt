package com.example.quickread.repository

import com.example.quickread.models.NewsResponse
import retrofit2.Response

/**
 * Contract for news data operations.
 */
interface NewsRepository {
    suspend fun getTopHeadlines(country: String, page: Int, category: String? = null): Response<NewsResponse>
    suspend fun searchNews(query: String, page: Int): Response<NewsResponse>
}
package com.example.quickread.api

import com.example.quickread.BuildConfig
import com.example.quickread.models.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for the [NewsAPI](https://newsapi.org/v2/) endpoints.
 */
interface NewsAPI {

    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("page") pageNumber: Int = 1,
        @Query("category") category: String? = null,
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY
    ): Response<NewsResponse>

    @GET("everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("page") pageNumber: Int = 1,
        @Query("language") language: String = "en",
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY
    ): Response<NewsResponse>
}
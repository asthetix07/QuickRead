package com.example.myapplication.repository

import com.example.myapplication.api.NewsAPI
import com.example.myapplication.models.NewsResponse
import retrofit2.Response
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val api: NewsAPI
) : NewsRepository {

    override suspend fun getTopHeadlines(country: String, page: Int): Response<NewsResponse> {
        return api.getTopHeadlines(country, page)
    }

    override suspend fun searchNews(query: String, page: Int): Response<NewsResponse> {
        return api.searchNews(query, page)
    }
}

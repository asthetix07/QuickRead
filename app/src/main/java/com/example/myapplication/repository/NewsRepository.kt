package com.example.myapplication.repository

import com.example.myapplication.api.RetrofitInstance.Companion.api
import com.example.myapplication.models.NewsResponse
import retrofit2.Response

interface NewsRepository {
    suspend fun getTopHeadlines(country: String, page: Int): Response<NewsResponse> {
        return api.getTopHeadlines(country, page)
    }
    suspend fun searchNews(query: String, page: Int): Response<NewsResponse>{
        return api.searchNews(query)
    }
}

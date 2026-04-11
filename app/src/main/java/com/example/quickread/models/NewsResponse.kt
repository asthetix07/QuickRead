package com.example.quickread.models

/**
 * Top-level API response wrapper for news endpoints.
 */
data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)
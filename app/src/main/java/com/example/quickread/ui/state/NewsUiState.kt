package com.example.quickread.ui.state

import com.example.quickread.models.Article

/**
 * Represents the UI state for news-related screens.
 */
data class NewsUiState(
    val isLoading: Boolean = false,
    val articles: List<Article> = emptyList(),
    val error: String? = null
)

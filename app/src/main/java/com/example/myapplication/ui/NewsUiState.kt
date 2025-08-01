package com.example.myapplication.ui

import com.example.myapplication.models.Article

data class NewsUiState(
    val isLoading: Boolean = false,
    val articles: List<Article> = emptyList(),
    val error: String? = null
)

package com.example.quickread.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a news article. Serves as both the API response model
 * and the Room entity for local persistence.
 */
@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String?,
    val urlToImage: String?,
    val savedAt: Long = System.currentTimeMillis()
)
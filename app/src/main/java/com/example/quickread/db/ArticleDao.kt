package com.example.quickread.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quickread.models.Article

/**
 * Data Access Object for saved [Article] entities.
 */
@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNews(news: Article): Long

    @Query("SELECT * FROM articles ORDER BY id DESC")
    fun getAllNews(): LiveData<List<Article>>

    @Query("SELECT * FROM articles WHERE url = :url LIMIT 1")
    suspend fun getArticleByUrl(url: String): Article?

    @Delete
    suspend fun deleteNews(news: Article)
}
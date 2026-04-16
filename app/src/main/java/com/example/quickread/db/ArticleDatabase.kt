package com.example.quickread.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.quickread.models.Article

/**
 * Room database for locally persisted articles.
 *
 * Hilt provides the singleton instance via
 * [com.example.quickread.di.AppModule.provideDatabase].
 */
@Database(entities = [Article::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun getArticleDao(): ArticleDao
}


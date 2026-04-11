package com.example.quickread.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.quickread.models.Article

/**
 * Room database for locally persisted articles.
 *
 * Uses a singleton pattern via [invoke] to ensure a single database
 * instance across the application. In practice, Hilt provides the
 * instance through [com.example.quickread.di.AppModule].
 */
@Database(entities = [Article::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao

    companion object {
        @Volatile
        private var instance: NewsDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                NewsDatabase::class.java,
                "article_db.db"
            ).build().also { instance = it }
        }
    }
}

package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.api.NewsAPI
import com.example.myapplication.db.ArticleDao
import com.example.myapplication.db.NewsDatabase
import com.example.myapplication.repository.NewsRepository
import com.example.myapplication.repository.NewsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit): NewsAPI {
        return retrofit.create(NewsAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideRepository(api: NewsAPI): NewsRepository {
        return NewsRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NewsDatabase {
        return NewsDatabase.invoke(context)
    }

    @Provides
    @Singleton
    fun provideDao(db: NewsDatabase): ArticleDao {
        return db.getArticleDao()
    }

}

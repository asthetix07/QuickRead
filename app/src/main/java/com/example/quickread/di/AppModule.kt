package com.example.quickread.di

import android.content.Context
import com.example.quickread.api.NewsAPI
import com.example.quickread.db.ArticleDao
import com.example.quickread.db.NewsDatabase
import com.example.quickread.repository.NewsRepository
import com.example.quickread.repository.NewsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module providing application-scoped singletons for networking,
 * database, and repository layers.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://newsapi.org/v2/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit): NewsAPI =
        retrofit.create(NewsAPI::class.java)

    @Provides
    @Singleton
    fun provideRepository(api: NewsAPI): NewsRepository =
        NewsRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NewsDatabase =
        NewsDatabase.invoke(context)

    @Provides
    @Singleton
    fun provideDao(db: NewsDatabase): ArticleDao =
        db.getArticleDao()
}

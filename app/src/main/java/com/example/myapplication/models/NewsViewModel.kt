package com.example.myapplication.models

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.db.ArticleDao
import com.example.myapplication.repository.NewsRepository
import com.example.myapplication.ui.NewsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository,
    private val dao: ArticleDao
) : ViewModel() {

    val topNews = MutableStateFlow(NewsUiState())
    val savedNews: LiveData<List<Article>> = dao.getAllNews()
    private val _searchNewsState = MutableStateFlow(NewsUiState())
    val searchNewsState: StateFlow<NewsUiState> = _searchNewsState

    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false


    fun fetchTopHeadlines() = viewModelScope.launch {
        if (isLoading || isLastPage) return@launch
        isLoading = true
        topNews.value = topNews.value.copy(isLoading = true)

        try {
            val response = repository.getTopHeadlines("us", currentPage)

            Log.d("NewsViewModel", "Page: $currentPage, Articles: ${response.body()?.articles?.size}")
            Log.d("NewsViewModel", "Code: ${response.code()}, Msg: ${response.message()}")

            if (response.isSuccessful) {
                val newArticles = response.body()?.articles ?: emptyList()
                val allArticles = topNews.value.articles + newArticles

                topNews.value = NewsUiState(articles = allArticles)

                if (newArticles.isEmpty()) {
                    isLastPage = true
                } else {
                    currentPage++
                }
            } else {
                topNews.value = NewsUiState(error = "Failed to load news.")
            }
        } catch (e: Exception) {
            topNews.value = NewsUiState(error = e.message)
        } finally {
            isLoading = false
        }
    }

    fun searchNews(query: String) = viewModelScope.launch {
        _searchNewsState.value = NewsUiState(isLoading = true)

        try {
            val response = repository.searchNews(query, 1)

            if (response.isSuccessful) {
                val articles = response.body()?.articles ?: emptyList()
                _searchNewsState.value = NewsUiState(articles = articles)
            } else {
                _searchNewsState.value = NewsUiState(error = "Failed to fetch search results.")
            }
        } catch (e: Exception) {
            _searchNewsState.value = NewsUiState(error = e.localizedMessage ?: "Unknown error")
        }
    }

    private fun Article.toLocalArticle(defaultImageUrl: String): Article {
        return Article(
            author = this.author ?: "Unknown",
            content = this.content ?: "",
            description = this.description ?: "No desc. available",
            publishedAt = this.publishedAt ?: "N/A",
            source = this.source ?: Source("", "Unknown Source"),  // fallback source
            title = this.title ?: "No title available",
            url = this.url ?: "",  // this shouldn't be null, but just in case
            urlToImage = this.urlToImage ?: defaultImageUrl
        )
    }


    fun saveArticle(article: Article, context: Context) = viewModelScope.launch {
        val defaultImage = "android.resource://com.example.myapplication/drawable/no_cover_image_01"
        val localArticle = article.toLocalArticle(defaultImage)

        val existing = localArticle.url?.let { dao.getArticleByUrl(it) }

        if (existing != null) {
            Toast.makeText(context, "Article already saved", Toast.LENGTH_SHORT).show()
        } else {
            val result = dao.insertNews(localArticle)
            if (result != -1L) {
                Toast.makeText(context, "Article saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to save article", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        dao.deleteNews(article)
    }

}

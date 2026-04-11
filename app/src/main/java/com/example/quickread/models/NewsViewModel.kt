package com.example.quickread.models

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickread.db.ArticleDao
import com.example.quickread.repository.NewsRepository
import com.example.quickread.ui.state.NewsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Central ViewModel for news operations — fetching headlines, searching,
 * saving/deleting articles, and managing category selection.
 */
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository,
    private val dao: ArticleDao
) : ViewModel() {

    private val _topNews = MutableStateFlow(NewsUiState())
    val topNews: StateFlow<NewsUiState> = _topNews.asStateFlow()

    val savedNews: LiveData<List<Article>> = dao.getAllNews()

    private val _searchNewsState = MutableStateFlow(NewsUiState())
    val searchNewsState: StateFlow<NewsUiState> = _searchNewsState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false

    // ── Headlines ────────────────────────────────────────────────────────

    /**
     * Fetches the next page of top headlines. Respects loading and
     * last-page guards to prevent duplicate or unnecessary requests.
     */
    fun fetchTopHeadlines() {
        if (isLoading || isLastPage) return
        isLoading = true

        viewModelScope.launch {
            _topNews.value = _topNews.value.copy(isLoading = true)
            try {
                val response = repository.getTopHeadlines(
                    "us", currentPage, _selectedCategory.value
                )
                if (response.isSuccessful) {
                    val newArticles = response.body()?.articles.orEmpty()
                    val allArticles = _topNews.value.articles + newArticles
                    _topNews.value = NewsUiState(articles = allArticles)

                    if (newArticles.isEmpty()) isLastPage = true else currentPage++
                } else {
                    _topNews.value = _topNews.value.copy(
                        isLoading = false, error = "Failed to load news."
                    )
                }
            } catch (e: Exception) {
                _topNews.value = _topNews.value.copy(
                    isLoading = false, error = e.message
                )
            } finally {
                isLoading = false
            }
        }
    }

    // ── Category ─────────────────────────────────────────────────────────

    /**
     * Selects a news category and reloads top headlines.
     * Passing `null` clears the filter (shows all categories).
     */
    fun selectCategory(category: String?) {
        if (_selectedCategory.value == category) return
        _selectedCategory.value = category
        currentPage = 1
        isLastPage = false
        isLoading = false
        _topNews.value = NewsUiState()
        fetchTopHeadlines()
    }

    // ── Search ───────────────────────────────────────────────────────────

    /**
     * Searches for articles matching the given [query].
     */
    fun searchNews(query: String) {
        viewModelScope.launch {
            _searchNewsState.value = NewsUiState(isLoading = true)
            try {
                val response = repository.searchNews(query, 1)
                if (response.isSuccessful) {
                    val articles = response.body()?.articles.orEmpty()
                    _searchNewsState.value = NewsUiState(articles = articles)
                } else {
                    _searchNewsState.value = NewsUiState(error = "Failed to fetch search results.")
                }
            } catch (e: Exception) {
                _searchNewsState.value = NewsUiState(
                    error = e.localizedMessage ?: "Unknown error"
                )
            }
        }
    }

    // ── Save / Delete ────────────────────────────────────────────────────

    /**
     * Saves an article locally. Shows a toast for duplicate, success, or failure.
     */
    fun saveArticle(article: Article, context: Context) {
        viewModelScope.launch {
            val localArticle = article.toLocalArticle()
            val existing = withContext(Dispatchers.IO) {
                localArticle.url?.let { dao.getArticleByUrl(it) }
            }

            if (existing != null) {
                Toast.makeText(context, "Article already saved.", Toast.LENGTH_SHORT).show()
            } else {
                val result = withContext(Dispatchers.IO) { dao.insertNews(localArticle) }
                if (result != -1L) {
                    Toast.makeText(context, "Article saved.", Toast.LENGTH_SHORT).show()
                    downloadArticleOffline(article.url, context)
                } else {
                    Toast.makeText(context, "Failed to save article.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Deletes a saved article and its offline cache file.
     */
    fun deleteArticle(article: Article, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteNews(article)
            val fileName = "${article.url?.hashCode()}.mht"
            val file = java.io.File(context.filesDir, "saved_articles/$fileName")
            if (file.exists()) file.delete()
        }
    }

    // ── Private helpers ──────────────────────────────────────────────────

    /**
     * Downloads the article's web page as a .mht archive for offline reading.
     */
    private fun downloadArticleOffline(url: String?, context: Context) {
        if (url.isNullOrBlank()) return

        viewModelScope.launch(Dispatchers.Main) {
            val dir = java.io.File(context.filesDir, "saved_articles")
            if (!dir.exists()) dir.mkdirs()

            val fileName = "${url.hashCode()}.mht"
            val file = java.io.File(dir, fileName)
            if (file.exists()) return@launch

            try {
                val webView = android.webkit.WebView(context)
                webView.settings.javaScriptEnabled = true
                webView.webViewClient = object : android.webkit.WebViewClient() {
                    override fun onPageFinished(view: android.webkit.WebView, loadedUrl: String) {
                        super.onPageFinished(view, loadedUrl)
                        view.saveWebArchive(file.absolutePath, false) { view.destroy() }
                    }
                }
                webView.loadUrl(url)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Maps a remote article to a local-safe copy with null-safe defaults.
     */
    private fun Article.toLocalArticle(): Article {
        return Article(
            author = this.author ?: "Unknown",
            content = this.content ?: "",
            description = this.description ?: "No desc. available",
            publishedAt = this.publishedAt ?: "N/A",
            source = this.source ?: Source("", "Unknown Source"),
            title = this.title ?: "No title available",
            url = this.url ?: "",
            urlToImage = this.urlToImage ?: ""
        )
    }
}

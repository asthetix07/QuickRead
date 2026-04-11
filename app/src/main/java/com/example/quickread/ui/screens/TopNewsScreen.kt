package com.example.quickread.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.quickread.models.NewsViewModel
import com.example.quickread.ui.components.CategoryChipBar
import com.example.quickread.ui.components.ErrorState
import com.example.quickread.ui.components.LoadingIndicator
import com.example.quickread.ui.components.NewsCard
import com.example.quickread.ui.theme.TanBrown

@Composable
fun TopNewsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: NewsViewModel = hiltViewModel(),
) {
    val newsState by viewModel.topNews.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchTopHeadlines()
    }

    // Memoize distinct articles to avoid re-computing on every recomposition
    val distinctArticles = remember(newsState.articles) {
        newsState.articles.distinctBy { it.url }
    }

    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= (layoutInfo.totalItemsCount - 3)
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !newsState.isLoading) {
            viewModel.fetchTopHeadlines()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        when {
            newsState.isLoading && distinctArticles.isEmpty() -> {
                LoadingIndicator()
            }

            newsState.error != null && distinctArticles.isEmpty() -> {
                ErrorState(
                    message = "No Internet Connection",
                    onRetry = { viewModel.fetchTopHeadlines() }
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    // Category chip bar as the first scrollable item
                    item(key = "category_chips") {
                        CategoryChipBar(
                            selectedCategory = selectedCategory,
                            onCategorySelected = { viewModel.selectCategory(it) },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(
                        items = distinctArticles,
                        key = { it.url ?: it.hashCode().toString() }
                    ) { article ->
                        NewsCard(
                            article = article,
                            onSaveClick = { viewModel.saveArticle(article, context) },
                            onClick = {
                                val encodedUrl = Uri.encode(article.url)
                                navController.navigate("webView/$encodedUrl")
                            }
                        )
                    }

                    if (newsState.isLoading) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                CircularProgressIndicator(
                                    color = TanBrown,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

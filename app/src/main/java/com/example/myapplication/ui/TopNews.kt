package com.example.myapplication.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.example.myapplication.models.NewsViewModel

@Composable
fun TopNews(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: NewsViewModel = hiltViewModel(),
) {
    val newsState by viewModel.topNews.collectAsState()
    val listState = rememberLazyListState()
    val context = LocalContext.current  // 👈 Add this line


    LaunchedEffect(Unit) {
        viewModel.fetchTopHeadlines()
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
            newsState.isLoading && newsState.articles.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            newsState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.no_net), // 🧠 You must place your PNG in `res/drawable`
                            contentDescription = "No Internet",
                            modifier = Modifier
                                .size(200.dp)
                                .padding(bottom = 24.dp)
                        )

                        Text(
                            text = "No Internet Connection",
                            color = colorResource(id = R.color.p4),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        TextButton(
                            onClick = { viewModel.fetchTopHeadlines() },
                            modifier = Modifier.clip(shape = RoundedCornerShape(20.dp))
                                .background(color = colorResource(id = R.color.p2))
                        ) {
                            Text("Retry",
                                color = colorResource(id = R.color.p4)
                            )
                        }
                    }
                }

            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState
                ) {
                    items(newsState.articles.distinctBy { it.url }, key = { it.url!! }) { article ->
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


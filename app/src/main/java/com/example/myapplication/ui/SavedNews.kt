package com.example.myapplication.ui

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.example.myapplication.models.NewsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedNews(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val savedArticles by viewModel.savedNews.observeAsState(emptyList())
    val listState = rememberLazyListState()
    val context = LocalContext.current

    // Scroll to top when new article is added
    LaunchedEffect(savedArticles.size) {
        if (savedArticles.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (savedArticles.isEmpty()) {
            Text(
                text = "No news saved, browse news and mark ★",
                color = colorResource(id = R.color.p4),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(state = listState) {
//            items(savedArticles, key = { it.id ?: it.hashCode() }) { article ->
//                NewsCard(
//                    article = article,
//                    onClick = { val encodedUrl = Uri.encode(article.url)
//                        navController.navigate("webView/$encodedUrl") // open detail or web
//                    },
//                    onSaveClick = { viewModel.saveArticle(article, context) }
//                )
//            }

                items(savedArticles, key = { it.id ?: it.hashCode() }) { article ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                viewModel.deleteArticle(article)
                                Toast.makeText(context, "Article deleted", Toast.LENGTH_SHORT)
                                    .show()
                                true
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            // Red delete background with icon
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }
                        },
                        content = {
                            NewsCard(
                                article = article,
                                onClick = {
                                    val encodedUrl = Uri.encode(article.url)
                                    navController.navigate("webView/$encodedUrl")
                                },
                                onSaveClick = { viewModel.saveArticle(article, context) }
                            )
                        }
                    )
                }
            }
        }
    }
}



package com.example.quickread.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.quickread.models.NewsViewModel
import com.example.quickread.ui.components.NewsCard
import com.example.quickread.utils.NetworkUtils
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedNewsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val savedArticles by viewModel.savedNews.observeAsState(emptyList())
    val listState = rememberLazyListState()
    val context = LocalContext.current

    // Scroll to top when a new article is saved
    LaunchedEffect(savedArticles.size) {
        if (savedArticles.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (savedArticles.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No news saved, browse news and mark ★",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(
                    items = savedArticles,
                    key = { it.id ?: it.hashCode().toString() }
                ) { article ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                viewModel.deleteArticle(article, context)
                                Toast.makeText(context, "Article deleted", Toast.LENGTH_SHORT)
                                    .show()
                                true
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
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
                                    val isOffline = !NetworkUtils.isNetworkAvailable(context)
                                    val fileName = "${article.url?.hashCode()}.mht"
                                    val file = File(context.filesDir, "saved_articles/$fileName")

                                    val targetUrl = if (isOffline) {
                                        if (file.exists()) {
                                            "file://${file.absolutePath}"
                                        } else {
                                            Toast.makeText(context, "Offline article unavailable", Toast.LENGTH_SHORT).show()
                                            return@NewsCard
                                        }
                                    } else {
                                        article.url ?: ""
                                    }

                                    val encodedUrl = Uri.encode(targetUrl)
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

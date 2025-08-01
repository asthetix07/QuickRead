package com.example.myapplication.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.myapplication.models.NewsViewModel
import com.example.myapplication.R
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun SearchNews(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: NewsViewModel = hiltViewModel(),
) {
    var searchQuery by remember { mutableStateOf("") }
    val newsState by viewModel.searchNewsState.collectAsState()
    val context  = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Type 3 letters to go!") },
            singleLine = true, // ✅ ensures one line only
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done // ✅ sets the IME action to Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    if (searchQuery.length >= 3) {
                        viewModel.searchNews(searchQuery)
                    }
                }
            ),
            modifier = Modifier.fillMaxWidth().padding(start = 7.dp , end = 7.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            newsState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            newsState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.no_net),
                            contentDescription = "Error Image",
                            modifier = Modifier
                                .size(200.dp)
                                .padding(bottom = 16.dp)
                        )

                        Text(
                            text = "Failed to load search results",
                            color = colorResource(id = R.color.p4),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(
                            onClick = {
                                if (searchQuery.length >= 3) {
                                    viewModel.searchNews(searchQuery)
                                }
                            },
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

            newsState.articles.isEmpty() && searchQuery.length >= 3 -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No results found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorResource(id = R.color.p4)
                    )
                }
            }

            else -> {
                LazyColumn {
                    items(newsState.articles, key = { it.url!! }) { article ->
                        NewsCard(
                            article = article,
                            onClick = {
                                val encodedUrl = Uri.encode(article.url)
                                navController.navigate("webView/$encodedUrl")
                            },
                            onSaveClick = { viewModel.saveArticle(article, context) }
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(searchQuery) {
        snapshotFlow { searchQuery }
            .debounce(500)
            .collectLatest { query ->
                if (query.length >= 3) {
                    viewModel.searchNews(query)
                }
            }
    }
}




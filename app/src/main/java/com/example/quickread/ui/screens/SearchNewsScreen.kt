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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.quickread.viewmodel.NewsViewModel
import com.example.quickread.ui.components.ErrorState
import com.example.quickread.ui.components.LoadingIndicator
import com.example.quickread.ui.components.NewsCard
import com.example.quickread.ui.components.NewsSummaryBottomSheet
import com.example.quickread.models.Article
import com.example.quickread.ui.theme.LightBlueGray
import com.example.quickread.ui.theme.MediumBlue
import com.example.quickread.ui.theme.TanBrown
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@OptIn(FlowPreview::class)
@Composable
fun SearchNewsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: NewsViewModel = hiltViewModel(),
) {
    var searchQuery by remember { mutableStateOf("") }
    val newsState by viewModel.searchNewsState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var hasSearched by remember { mutableStateOf(false) }
    var selectedArticleForSummary by remember { mutableStateOf<Article?>(null) }

    // Memoize distinct articles to avoid re-computing on every recomposition
    val distinctArticles = remember(newsState.articles) {
        newsState.articles.distinctBy { it.url }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Type 3 letters to go!") },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    if (searchQuery.trim().length >= 3) {
                        hasSearched = true
                        viewModel.searchNews(searchQuery.trim())
                    }
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TanBrown,
                unfocusedBorderColor = MediumBlue,
                cursorColor = TanBrown,
                focusedLabelColor = TanBrown,
                unfocusedLabelColor = LightBlueGray,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        when {
            // Show the initial placeholder before any search has been triggered
            !hasSearched && distinctArticles.isEmpty() && !newsState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Make your first search",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            newsState.isLoading && distinctArticles.isEmpty() -> {
                LoadingIndicator()
            }

            newsState.error != null && distinctArticles.isEmpty() -> {
                ErrorState(
                    message = "Failed to load search results",
                    onRetry = {
                        if (searchQuery.trim().length >= 3) {
                            viewModel.searchNews(searchQuery.trim())
                        }
                    }
                )
            }

            distinctArticles.isEmpty() && searchQuery.trim().length >= 3 && !newsState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No results found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(
                        items = distinctArticles,
                        key = { it.url ?: it.hashCode().toString() }
                    ) { article ->
                        NewsCard(
                            article = article,
                            onClick = {
                                val encodedUrl = Uri.encode(article.url)
                                navController.navigate("webView/$encodedUrl")
                            },
                            onSaveClick = { viewModel.saveArticle(article, context) },
                            onGeminiClick = { selectedArticleForSummary = article }
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { searchQuery }
            .debounce(500)
            .map { it.trim() }
            .distinctUntilChanged()
            .filter { it.length >= 3 }
            .collectLatest { query ->
                hasSearched = true
                viewModel.searchNews(query)
            }
    }

    if (selectedArticleForSummary != null) {
        NewsSummaryBottomSheet(
            article = selectedArticleForSummary,
            onDismiss = { selectedArticleForSummary = null },
            viewModel = viewModel
        )
    }
}

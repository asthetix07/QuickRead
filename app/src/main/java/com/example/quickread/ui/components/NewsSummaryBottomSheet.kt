package com.example.quickread.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.quickread.R
import com.example.quickread.models.Article
import com.example.quickread.viewmodel.NewsViewModel
import com.example.quickread.ui.theme.DarkBlue
import com.example.quickread.ui.theme.LightBlueGray
import com.example.quickread.ui.theme.MediumBlue
import com.example.quickread.ui.theme.TanBrown
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Sealed class to distinguish between network errors and other failures.
 */
private sealed class SummaryError {
    data object NetworkError : SummaryError()
    data class OtherError(val message: String) : SummaryError()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsSummaryBottomSheet(
    article: Article?,
    onDismiss: () -> Unit,
    viewModel: NewsViewModel
) {
    if (article == null) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var summary by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var summaryError by remember { mutableStateOf<SummaryError?>(null) }

    LaunchedEffect(article) {
        isLoading = true
        summary = null
        summaryError = null
        try {
            val titleToSummarize = article.title
            if (!titleToSummarize.isNullOrBlank()) {
                summary = viewModel.generateSummary(titleToSummarize)
            } else {
                summaryError = SummaryError.OtherError("Summary not available")
            }
        } catch (e: Exception) {
            summaryError = when (e) {
                is UnknownHostException,
                is ConnectException,
                is SocketTimeoutException -> SummaryError.NetworkError
                else -> {
                    // Check if the cause is a network issue wrapped in another exception
                    val cause = e.cause
                    if (cause is UnknownHostException ||
                        cause is ConnectException ||
                        cause is SocketTimeoutException
                    ) {
                        SummaryError.NetworkError
                    } else {
                        SummaryError.OtherError("Summary not available")
                    }
                }
            }
        } finally {
            isLoading = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DarkBlue, MediumBlue)
                    ),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            // "AI Summary" title — uses Serif (Times New Roman) per requirement
            Text(
                text = "AI Summary",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content area — wraps content height to avoid extra blank space
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .heightIn(min = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        // Inline spinner with no background — fixes the white box issue
                        val infiniteTransition = rememberInfiniteTransition(label = "summary_loading")
                        val rotation by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "summary_spinner"
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(48.dp)
                                    .rotate(rotation),
                                color = TanBrown,
                                strokeWidth = 4.dp,
                                trackColor = LightBlueGray.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Generating summary…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    summaryError is SummaryError.NetworkError -> {
                        // No internet — show the no_net image fitted inside the dialog
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.no_net_1),
                                contentDescription = "No internet connection",
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .heightIn(max = 160.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No internet connection",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    summaryError is SummaryError.OtherError -> {
                        // Other errors (model limit, token expiry, etc.)
                        Text(
                            text = "Summary not available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                    }

                    else -> {
                        Text(
                            text = summary ?: "Summary not available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

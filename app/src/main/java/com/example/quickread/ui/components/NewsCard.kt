@file:Suppress("DEPRECATION")
package com.example.quickread.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.quickread.R
import com.example.quickread.models.Article
import com.example.quickread.ui.theme.TanBrown
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

/**
 * Reusable news article card that displays an image, title, description,
 * author, publish date, and a save action.
 *
 * @param article The [Article] data to display.
 * @param onClick Callback invoked when the card is tapped.
 * @param onSaveClick Callback invoked when the save/star icon is tapped.
 */
@Composable
fun NewsCard(
    article: Article,
    onClick: () -> Unit,
    onSaveClick: () -> Unit,
    onGeminiClick: () -> Unit = {}
) {
    val painter = rememberAsyncImagePainter(article.urlToImage ?: "")
    val imageState = painter.state
    val showFallbackImage =
        (article.urlToImage ?: "").isBlank() || imageState is AsyncImagePainter.State.Error
    val showShimmer =
        !showFallbackImage && imageState is AsyncImagePainter.State.Loading

    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Image + Author + Publish date
            Column(
                modifier = Modifier.width(110.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showFallbackImage) {
                    Image(
                        painter = painterResource(id = R.drawable.no_img),
                        contentDescription = "No image found",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painter,
                        contentDescription = "News Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .placeholder(
                                visible = showShimmer,
                                highlight = PlaceholderHighlight.shimmer()
                            )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Author: ${article.author ?: "Unknown"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .placeholder(
                            visible = showShimmer,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                )

                Text(
                    text = "Publish: ${article.publishedAt?.substring(0, 10) ?: "N/A"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .placeholder(
                            visible = showShimmer,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title + Description + Save action
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            ) {
                Text(
                    text = article.title ?: "",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .placeholder(
                            visible = showShimmer,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = article.description ?: "No desc. available",
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .placeholder(
                            visible = showShimmer,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                )

                // Save icon aligned to trailing edge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onGeminiClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.gemini),
                            contentDescription = "Gemini Icon",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(
                        onClick = onSaveClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Save",
                            tint = TanBrown
                        )
                    }
                }
            }
        }
    }
}

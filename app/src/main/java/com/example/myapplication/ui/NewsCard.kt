@file:Suppress("DEPRECATION")

package com.example.myapplication.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.myapplication.R
import com.example.myapplication.models.Article
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer


@Suppress("DEPRECATION")
@Composable
fun NewsCard(
    article: Article,
    onClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val painter = rememberAsyncImagePainter(article.urlToImage ?: "")
    val imageState = painter.state
    val showFallbackImage = (article.urlToImage ?: "").isBlank() || imageState is AsyncImagePainter.State.Error
    val showShimmer = !showFallbackImage && imageState is AsyncImagePainter.State.Loading

    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp) // padding inside the card
        ) {
            // Left: Image + Author + Publish
            Column(
                modifier = Modifier
                    .width(130.dp)
            ) {
                if (showFallbackImage) {
                    Image(
                        painter = painterResource(id = R.drawable.no_cover_image_01),
                        contentDescription = "No image found",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Image(
                        painter = painter,
                        contentDescription = "News Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .placeholder(
                                visible = showShimmer,
                                highlight = PlaceholderHighlight.shimmer()
                            )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Author: ${article.author ?: "Unknown"}",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .placeholder(visible = showShimmer, highlight = PlaceholderHighlight.shimmer())
                )

                Text(
                    text = "Publish: ${article.publishedAt?.substring(0, 10) ?: "N/A"}",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .placeholder(visible = showShimmer, highlight = PlaceholderHighlight.shimmer())
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right: Title + Description + Star icon (bottom aligned)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = article.title ?: "",
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .placeholder(visible = showShimmer, highlight = PlaceholderHighlight.shimmer())
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = article.description ?: "No desc. available",
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .placeholder(visible = showShimmer, highlight = PlaceholderHighlight.shimmer())
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Star icon bottom right aligned
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onSaveClick) {
                        Icon(Icons.Default.Star, contentDescription = "Save")
                    }
                }
            }
        }
    }
}





package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Post
import com.example.data.model.PostType
import com.example.ui.CircleViewModel
import com.example.ui.theme.CirclePurple
import com.example.ui.theme.CloseFriendsGreen
import com.example.ui.theme.PureWhite

@Composable
fun ExploreScreen(
    viewModel: CircleViewModel,
    onPostClick: (Post) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.exploreSearchQuery.collectAsState()
    val selectedTag by viewModel.selectedExploreTag.collectAsState()
    val posts by viewModel.posts.collectAsState()

    val trendingTags = listOf(
        "#filmphotography",
        "#cafestudy",
        "#goldenhour",
        "#soundtrack",
        "#vinylcommunity",
        "#shinjuku",
        "#ambientmusic"
    )

    // Curated Explore Moments
    val exploreMoments = listOf(
        ExploreItem("e1", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=600&auto=format&fit=crop&q=80", "Maya Lin", "#filmphotography", false),
        ExploreItem("e2", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80", "Leo Chen", "#soundtrack", true),
        ExploreItem("e3", "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=600&auto=format&fit=crop&q=80", "Elena R.", "#cafestudy", false),
        ExploreItem("e4", "https://images.unsplash.com/photo-1542051841857-5f90071e7989?w=600&auto=format&fit=crop&q=80", "Kai Tanaka", "#shinjuku", false),
        ExploreItem("e5", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&auto=format&fit=crop&q=80", "Sam Rivera", "#goldenhour", false),
        ExploreItem("e6", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80", "Alex Vance", "#vinylcommunity", false)
    )

    val filteredExploreMoments = remember(searchQuery, selectedTag) {
        exploreMoments.filter { item ->
            val matchQuery = searchQuery.isBlank() || item.author.contains(searchQuery, ignoreCase = true) || item.tag.contains(searchQuery, ignoreCase = true)
            val matchTag = selectedTag == null || item.tag.equals(selectedTag, ignoreCase = true)
            matchQuery && matchTag
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 8.dp)
    ) {
        // Search Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search friends, circles, or #tags...", fontSize = 14.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CirclePurple)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("explore_search_input"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CirclePurple
                ),
                singleLine = true
            )
        }

        // Trending Tags Bar
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(trendingTags) { tag ->
                val isSelected = selectedTag == tag
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) CirclePurple else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clickable {
                            if (isSelected) viewModel.selectExploreTag(null) else viewModel.selectExploreTag(tag)
                        }
                        .testTag("explore_tag_$tag")
                ) {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) PureWhite else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Explore Moments Grid
        Text(
            text = if (selectedTag != null) "Moments in $selectedTag" else "Moments from Outer Circles",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 90.dp, top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredExploreMoments, key = { it.id }) { item ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .testTag("explore_grid_item_${item.id}")
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.tag,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (item.isVideo) {
                            Surface(
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Video",
                                        tint = PureWhite,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Bottom Overlay with Author and Tag
                        Surface(
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Text(
                                    text = item.author,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = PureWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = item.tag,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CirclePurple,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class ExploreItem(
    val id: String,
    val imageUrl: String,
    val author: String,
    val tag: String,
    val isVideo: Boolean
)

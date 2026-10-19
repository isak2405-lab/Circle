package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.CircleViewModel
import com.example.ui.theme.*

@Composable
fun ExploreScreen(
    viewModel: CircleViewModel,
    onPostClick: (Post) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.exploreSearchQuery.collectAsState()
    val selectedTag by viewModel.selectedExploreTag.collectAsState()
    val friends = remember { viewModel.getFriends() }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Moments, 1: People

    val dailyTags = listOf(
        "#filmphotography",
        "#cafestudy",
        "#goldenhour",
        "#soundtrack",
        "#vinylcommunity",
        "#shinjuku"
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

    val filteredMoments = remember(searchQuery, selectedTag) {
        exploreMoments.filter { item ->
            val matchQuery = searchQuery.isBlank() || item.author.contains(searchQuery, ignoreCase = true) || item.tag.contains(searchQuery, ignoreCase = true)
            val matchTag = selectedTag == null || item.tag.equals(selectedTag, ignoreCase = true)
            matchQuery && matchTag
        }
    }

    val filteredPeople = remember(searchQuery, friends) {
        if (searchQuery.isBlank()) friends else friends.filter {
            it.name.contains(searchQuery, ignoreCase = true) || it.username.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(AppCanvas)
    ) {
        // Rounded Search Field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = {
                    Text(
                        "Search friends, circles, or #tags...",
                        fontSize = 14.sp,
                        color = PlaceholderHint
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = SecondaryText, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = SecondaryText)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("explore_search_input"),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPrimary,
                    unfocusedBorderColor = DividerOutline,
                    focusedContainerColor = SurfaceColor,
                    unfocusedContainerColor = SurfaceColor
                ),
                singleLine = true
            )
        }

        // People / Moments Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Moments", "People").forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) SelectedBackground else SurfaceColor,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, DividerOutline),
                    modifier = Modifier
                        .clickable { selectedTab = index }
                        .testTag("explore_tab_$index")
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 13.sp,
                            color = if (isSelected) BrandPrimary else SecondaryText
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Daily Tag Chips in Soft Lavender
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(dailyTags) { tag ->
                val isSelected = selectedTag == tag
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) BrandPrimary else SelectedBackground,
                    modifier = Modifier
                        .clickable {
                            if (isSelected) viewModel.selectExploreTag(null) else viewModel.selectExploreTag(tag)
                        }
                        .testTag("explore_tag_$tag")
                ) {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = if (isSelected) PureWhite else PrimaryText
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Content
        if (selectedTab == 0) {
            // Moments Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredMoments, key = { it.id }) { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DividerOutline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
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
                                        .size(26.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Video",
                                            tint = PureWhite,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }

                            // Bottom Caption Overlay
                            Surface(
                                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                                color = Color.Black.copy(alpha = 0.55f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
                                    Text(
                                        text = item.author,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = PureWhite,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Text(
                                        text = item.tag,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = PureWhite.copy(alpha = 0.8f),
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Friendly Compact Person Rows
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("explore_people_list"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredPeople) { person ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceColor,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DividerOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = person.avatarUrl,
                                contentDescription = person.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = person.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = PrimaryText
                                )
                                Text(
                                    text = "@${person.username}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = SecondaryText
                                    )
                                )
                                if (person.bio.isNotBlank()) {
                                    Text(
                                        text = person.bio,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                        color = PrimaryText,
                                        maxLines = 1
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = { /* Connect in Circle */ },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = if (person.isCloseFriend) "In circle" else "Add",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = if (person.isCloseFriend) CloseFriendsGreen else BrandPrimary
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

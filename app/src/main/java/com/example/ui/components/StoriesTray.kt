package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.User
import com.example.data.model.UserStory
import com.example.ui.theme.CirclePurple
import com.example.ui.theme.CloseFriendsGreen
import com.example.ui.theme.PureWhite

@Composable
fun StoriesTray(
    currentUser: User,
    stories: List<UserStory>,
    onStoryClick: (Int) -> Unit,
    onAddStoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "Your Moment"
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onAddStoryClick() }
                    .testTag("story_item_my_moment")
            ) {
                Box(
                    modifier = Modifier.size(68.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = currentUser.avatarUrl,
                        contentDescription = "Your avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    )

                    // Add badge (+)
                    Surface(
                        shape = CircleShape,
                        color = CirclePurple,
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .size(22.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add moment",
                                tint = PureWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Your circle",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Friends Stories
        itemsIndexed(stories) { index, story ->
            val ringBrush = when {
                story.isSeen -> Brush.linearGradient(listOf(Color(0xFFD4D4D8), Color(0xFFE4E4E7)))
                story.isCloseFriendsOnly -> Brush.sweepGradient(
                    listOf(
                        CloseFriendsGreen,
                        Color(0xFF34D399),
                        CloseFriendsGreen
                    )
                )
                else -> Brush.sweepGradient(
                    listOf(
                        CirclePurple,
                        Color(0xFFA29BFE),
                        CirclePurple
                    )
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onStoryClick(index) }
                    .testTag("story_item_${story.user.username}")
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .border(
                            width = if (story.isSeen) 1.5.dp else 2.5.dp,
                            brush = ringBrush,
                            shape = CircleShape
                        )
                        .padding(3.5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = story.user.avatarUrl,
                        contentDescription = "${story.user.name}'s story",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.widthIn(max = 68.dp)
                ) {
                    if (story.isCloseFriendsOnly && !story.isSeen) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .border(1.dp, CloseFriendsGreen, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                    Text(
                        text = story.user.name.split(" ")[0],
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 11.sp,
                            fontWeight = if (!story.isSeen) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

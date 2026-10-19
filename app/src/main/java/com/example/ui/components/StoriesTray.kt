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
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.CloseFriendsGreen
import com.example.ui.theme.DividerOutline
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SecondaryText

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
        // "Your moment" / Add Control
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onAddStoryClick() }
                    .testTag("story_item_my_moment")
            ) {
                Box(
                    modifier = Modifier.size(66.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = currentUser.avatarUrl,
                        contentDescription = "Your moment",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .border(1.dp, DividerOutline, CircleShape)
                    )

                    // Add badge (+)
                    Surface(
                        shape = CircleShape,
                        color = BrandPrimary,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add moment",
                                tint = PureWhite,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Your moment",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = PrimaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Friends Stories (Purple unseen rings, muted seen rings)
        itemsIndexed(stories) { index, story ->
            val ringColor = when {
                story.isSeen -> DividerOutline
                story.isCloseFriendsOnly -> CloseFriendsGreen
                else -> BrandPrimary
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onStoryClick(index) }
                    .testTag("story_item_${story.user.username}")
            ) {
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .border(
                            width = if (story.isSeen) 1.dp else 2.dp,
                            color = ringColor,
                            shape = CircleShape
                        )
                        .padding(3.5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = story.user.avatarUrl,
                        contentDescription = "${story.user.name}'s moment",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = story.user.name.split(" ")[0],
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 11.sp,
                        fontWeight = if (!story.isSeen) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    color = if (!story.isSeen) PrimaryText else SecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.Post
import com.example.data.model.ReactionType
import com.example.ui.CircleViewModel
import com.example.ui.Screen
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.PostCard
import com.example.ui.components.StoriesTray
import com.example.ui.theme.*

@Composable
fun FeedScreen(
    viewModel: CircleViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val activeCommentPostId by viewModel.activeCommentPostId.collectAsStateWithLifecycle()
    val reactionBurst by viewModel.reactionBurst.collectAsStateWithLifecycle()

    val unreadNotifications = remember(notifications) {
        notifications.count { !it.isRead }
    }

    // Three quiet tabs: 0 = "All moments", 1 = "Close friends", 2 = "Following"
    var selectedFeedTab by remember { mutableIntStateOf(0) }

    val displayedPosts = remember(posts, selectedFeedTab) {
        when (selectedFeedTab) {
            0 -> posts // All moments
            1 -> posts.filter { it.isCloseFriendsOnly } // Close friends
            else -> posts.filter { it.author.id != currentUser.id } // Following
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppCanvas)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Clean White Top App Bar
            Surface(
                color = SurfaceColor,
                tonalElevation = 0.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, DividerOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Circle logo mark + lowercase "circle." wordmark with purple dot
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.testTag("app_brand_header")
                    ) {
                        // Official Circle logo mark
                        Icon(
                            painter = painterResource(id = R.drawable.ic_circle_logo),
                            contentDescription = "Circle Logo",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "circle",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp,
                                fontSize = 21.sp
                            ),
                            color = PrimaryText
                        )
                        Text(
                            text = ".",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 21.sp
                            ),
                            color = BrandPrimary
                        )
                    }

                    // Right: Notification Bell (with purple unread dot) + Small Avatar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Notification Bell
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .clickable { viewModel.navigateTo(Screen.Notifications) }
                                .testTag("notifications_bell_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = PrimaryText,
                                modifier = Modifier.size(22.dp)
                            )
                            if (unreadNotifications > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(BrandPrimary)
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-4).dp, y = 4.dp)
                                )
                            }
                        }

                        // Small Avatar
                        AsyncImage(
                            model = currentUser.avatarUrl,
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .border(1.dp, DividerOutline, CircleShape)
                                .clickable { viewModel.navigateTo(Screen.Profile) }
                                .testTag("top_bar_avatar")
                        )
                    }
                }
            }

            // Feed Content Stream
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("feed_list"),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // Header: "Your circle." + Subtitle: "Real life looks good on you."
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Your circle",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    letterSpacing = (-0.4).sp
                                ),
                                color = PrimaryText
                            )
                            Text(
                                text = ".",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 28.sp
                                ),
                                color = BrandPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Real life looks good on you.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = SecondaryText
                        )
                    }
                }

                // Three Quiet Tabs: All moments, Close friends, Following
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val tabs = listOf("All moments", "Close friends", "Following")
                        tabs.forEachIndexed { index, tabTitle ->
                            val isSelected = selectedFeedTab == index
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) SelectedBackground else SurfaceColor,
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, DividerOutline),
                                modifier = Modifier
                                    .clickable { selectedFeedTab = index }
                                    .testTag("feed_tab_$index")
                            ) {
                                Text(
                                    text = tabTitle,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        fontSize = 13.sp,
                                        color = if (isSelected) BrandPrimary else SecondaryText
                                    ),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Horizontal Moments Strip (with purple unseen rings, muted seen rings and Your moment/add control)
                item {
                    StoriesTray(
                        currentUser = currentUser,
                        stories = stories,
                        onStoryClick = { index -> viewModel.openStoryViewer(index) },
                        onAddStoryClick = { viewModel.navigateTo(Screen.CreatePost) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Slim Composer: "A little moment worth sharing?"
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceColor,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DividerOutline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { viewModel.navigateTo(Screen.CreatePost) }
                            .testTag("slim_feed_composer")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = currentUser.avatarUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "A little moment worth sharing?",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = PlaceholderHint,
                                    fontSize = 14.sp
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = SecondaryText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Posts Feed (Image-led vertical feed)
                if (displayedPosts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "All caught up with your people.",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = PrimaryText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "A little less noise. A little more you.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SecondaryText
                                )
                            }
                        }
                    }
                } else {
                    items(displayedPosts, key = { it.id }) { post ->
                        PostCard(
                            post = post,
                            onReactionClick = { reaction ->
                                viewModel.reactToPost(post.id, reaction)
                            },
                            onCommentClick = {
                                viewModel.openComments(post.id)
                            },
                            onSaveClick = {
                                viewModel.toggleSavePost(post.id)
                            }
                        )
                    }

                    // Feed footer warm copy
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "All caught up with your people.",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryText
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "A little less noise. A little more you.",
                                style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText)
                            )
                        }
                    }
                }
            }
        }

        // Active Comments Bottom Sheet
        if (activeCommentPostId != null) {
            val selectedPost = posts.find { it.id == activeCommentPostId }
            if (selectedPost != null) {
                CommentsBottomSheet(
                    post = selectedPost,
                    onDismiss = { viewModel.closeComments() },
                    onAddComment = { text ->
                        viewModel.addComment(selectedPost.id, text)
                    }
                )
            }
        }

        // Floating Reaction Burst
        AnimatedVisibility(
            visible = reactionBurst != null,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            val burst = reactionBurst
            if (burst != null) {
                Surface(
                    shape = CircleShape,
                    color = PureWhite,
                    shadowElevation = 6.dp,
                    modifier = Modifier.size(68.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = burst.second.emoji,
                            fontSize = 36.sp
                        )
                    }
                }
            }
        }
    }
}

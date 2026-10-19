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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FeedDisplayDensity
import com.example.data.model.FeedFilterMode
import com.example.data.model.Post
import com.example.data.model.PostType
import com.example.data.model.ReactionType
import com.example.ui.CircleViewModel
import com.example.ui.Screen
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.PostCard
import com.example.ui.components.StoriesTray
import com.example.ui.theme.CirclePurple
import com.example.ui.theme.CirclePurpleContainer
import com.example.ui.theme.CloseFriendsGreen
import com.example.ui.theme.CloseFriendsGreenContainer
import com.example.ui.theme.PureWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: CircleViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val feedFilterMode by viewModel.feedFilterMode.collectAsStateWithLifecycle()
    val feedDisplayDensity by viewModel.feedDisplayDensity.collectAsStateWithLifecycle()
    val activeCommentPostId by viewModel.activeCommentPostId.collectAsStateWithLifecycle()
    val reactionBurst by viewModel.reactionBurst.collectAsStateWithLifecycle()

    var showFeedOptionsSheet by remember { mutableStateOf(false) }

    val totalUnreadMessages = remember(conversations) {
        conversations.sumOf { it.unreadCount }
    }

    // Filter posts dynamically according to selected Feed Option
    val filteredPosts = remember(posts, feedFilterMode) {
        when (feedFilterMode) {
            FeedFilterMode.ALL_CHRONOLOGICAL -> posts
            FeedFilterMode.CLOSE_FRIENDS_ONLY -> posts.filter { it.isCloseFriendsOnly }
            FeedFilterMode.VISUAL_MOMENTS -> posts.filter { it.type == PostType.PHOTO || it.type == PostType.VIDEO }
            FeedFilterMode.QUIET_THOUGHTS -> posts.filter { it.type == PostType.THOUGHT }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Circle Minimal Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Name "circle" with accent dot
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("app_brand_title")
                ) {
                    Text(
                        text = "circle",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp,
                            fontSize = 28.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(CirclePurple)
                    )
                }

                // Actions: Security Vault + Feed Options + DMs button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Security Vault Shortcut Badge
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CloseFriendsGreen.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { viewModel.navigateTo(Screen.SecurityVault) }
                            .testTag("security_vault_entry_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Security Vault",
                                tint = CloseFriendsGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Feed Options Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (feedFilterMode != FeedFilterMode.ALL_CHRONOLOGICAL) CloseFriendsGreenContainer else MaterialTheme.colorScheme.surface,
                        border = if (feedFilterMode != FeedFilterMode.ALL_CHRONOLOGICAL) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .height(36.dp)
                            .clickable { showFeedOptionsSheet = true }
                            .testTag("feed_options_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Feed Options",
                                tint = if (feedFilterMode != FeedFilterMode.ALL_CHRONOLOGICAL) CloseFriendsGreen else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = when (feedFilterMode) {
                                    FeedFilterMode.ALL_CHRONOLOGICAL -> "Feed"
                                    FeedFilterMode.CLOSE_FRIENDS_ONLY -> "Close Circle"
                                    FeedFilterMode.VISUAL_MOMENTS -> "Visual"
                                    FeedFilterMode.QUIET_THOUGHTS -> "Thoughts"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                ),
                                color = if (feedFilterMode != FeedFilterMode.ALL_CHRONOLOGICAL) CloseFriendsGreen else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Direct Messages Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            .clickable { viewModel.navigateTo(Screen.ChatList) }
                            .testTag("dms_entry_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Direct Messages",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )

                        if (totalUnreadMessages > 0) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(CirclePurple)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                }
            }

            // Feed Content: Stories Tray at top + Vertical Posts
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("feed_list"),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // Stories Tray (Disappearing 24h Moments)
                item {
                    StoriesTray(
                        currentUser = currentUser,
                        stories = stories,
                        onStoryClick = { index ->
                            viewModel.openStoryViewer(index)
                        },
                        onAddStoryClick = {
                            viewModel.navigateTo(Screen.CreatePost)
                        }
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Feed Posts
                if (filteredPosts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No moments in this filter mode",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Switch to 'Live Chronological' or post a new moment.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.setFeedFilterMode(FeedFilterMode.ALL_CHRONOLOGICAL) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CirclePurple)
                                ) {
                                    Text("Reset Filter", color = PureWhite)
                                }
                            }
                        }
                    }
                } else {
                    items(filteredPosts, key = { it.id }) { post ->
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

        // Feed Options Bottom Sheet
        if (showFeedOptionsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFeedOptionsSheet = false },
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Feed Options & Preferences",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        IconButton(onClick = { showFeedOptionsSheet = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "FEED CURATION MODE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FeedFilterMode.entries.forEach { mode ->
                        val isSelected = feedFilterMode == mode
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) CirclePurpleContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, CirclePurple) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.setFeedFilterMode(mode)
                                    showFeedOptionsSheet = false
                                }
                                .testTag("feed_mode_${mode.name.lowercase()}")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = mode.label,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) CirclePurple else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = mode.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Shortcut to Security Vault
                    Button(
                        onClick = {
                            showFeedOptionsSheet = false
                            viewModel.navigateTo(Screen.SecurityVault)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CloseFriendsGreen),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = PureWhite, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Security & Privacy Vault", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }
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
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = burst.second.emoji,
                            fontSize = 38.sp
                        )
                    }
                }
            }
        }
    }
}

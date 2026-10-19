package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Post
import com.example.data.model.PostType
import com.example.data.model.ReactionType
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PostCard(
    post: Post,
    onReactionClick: (ReactionType) -> Unit,
    onCommentClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    var showDoubleTapHeart by remember { mutableStateOf(false) }
    var isVideoPlaying by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("post_card_${post.id}"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            // Header: Author Avatar, Name, Location/Time, Close Friends badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Author Avatar with ring
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .border(
                            width = if (post.isCloseFriendsOnly) 2.dp else 1.dp,
                            color = if (post.isCloseFriendsOnly) CloseFriendsGreen else MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                        .padding(2.5.dp)
                ) {
                    AsyncImage(
                        model = post.author.avatarUrl,
                        contentDescription = post.author.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.author.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${post.timestamp}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (post.location != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 1.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = CirclePurple,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = post.location,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Close Friends Badge
                if (post.isCloseFriendsOnly) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CloseFriendsGreenContainer,
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(CloseFriendsGreen)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Close Circle",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = CloseFriendsGreen
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body: Photo, Video, or Text Thought
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                onReactionClick(ReactionType.HEART)
                                showDoubleTapHeart = true
                                coroutineScope.launch {
                                    delay(850)
                                    showDoubleTapHeart = false
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                when (post.type) {
                    PostType.PHOTO -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.05f)
                                .background(DividerColor)
                        ) {
                            AsyncImage(
                                model = post.mediaUrl,
                                contentDescription = post.caption,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Aesthetic filter overlay badge if applied
                            if (post.filterName != "Original") {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PureBlack.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = post.filterName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = PureWhite,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    PostType.VIDEO -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.1f)
                                .background(DividerColor)
                        ) {
                            AsyncImage(
                                model = post.mediaUrl,
                                contentDescription = "Video moment",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Video overlay controls
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = PureBlack.copy(alpha = 0.65f),
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clickable { isVideoPlaying = !isVideoPlaying }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Play video",
                                            tint = PureWhite,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                            }

                            // Video badge (max 60s)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PureBlack.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = PureWhite,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isVideoPlaying) "Playing 0:34" else "0:48 / 0:60",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = PureWhite,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    PostType.THOUGHT -> {
                        val thoughtGradients = listOf(
                            listOf(Color(0xFF6C5CE7), Color(0xFFA29BFE)),
                            listOf(Color(0xFF1E1B2E), Color(0xFF2D264A)),
                            listOf(Color(0xFF0F172A), Color(0xFF1E293B)),
                            listOf(Color(0xFF10B981), Color(0xFF059669))
                        )
                        val selectedGradient = thoughtGradients.getOrElse(post.thoughtGradientIndex) { thoughtGradients[0] }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 190.dp)
                                .background(Brush.linearGradient(selectedGradient))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "“",
                                    fontSize = 44.sp,
                                    lineHeight = 24.sp,
                                    color = PureWhite.copy(alpha = 0.4f),
                                    fontFamily = FontFamily.Serif
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = post.thoughtText ?: "",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = PureWhite,
                                        fontSize = 17.sp,
                                        lineHeight = 25.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    fontStyle = FontStyle.Normal
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "— ${post.author.name}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = PureWhite.copy(alpha = 0.7f),
                                        letterSpacing = 1.2.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Double Tap Animated Heart Overlay
                androidx.compose.animation.AnimatedVisibility(
                    visible = showDoubleTapHeart,
                    enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Text(
                        text = "❤️",
                        fontSize = 80.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactions Row:
            // 3 Reactions ONLY: ❤️, 😂, 🔥 (No public like counts)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reaction Pill Container
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.height(38.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ReactionType.entries.forEach { reactionType ->
                            val isSelected = post.userReaction == reactionType
                            val scale by animateFloatAsState(
                                targetValue = if (isSelected) 1.15f else 1.0f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "reactionScale"
                            )

                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) CirclePurpleContainer else Color.Transparent
                                    )
                                    .clickable { onReactionClick(reactionType) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("reaction_${reactionType.name.lowercase()}_${post.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = reactionType.emoji,
                                    fontSize = 17.sp,
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Comments Icon
                IconButton(
                    onClick = onCommentClick,
                    modifier = Modifier.testTag("comment_button_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Save / Bookmark Icon
                IconButton(
                    onClick = onSaveClick,
                    modifier = Modifier.testTag("save_button_${post.id}")
                ) {
                    Icon(
                        imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save moment",
                        tint = if (post.isSaved) CirclePurple else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Reaction Summary: NO like counts, just human-centric circle summary
            Text(
                text = post.reactionSummary,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )

            // Caption
            if (post.caption.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "${post.author.username}  ${post.caption}",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Tagged Friends
            if (post.taggedFriends.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    post.taggedFriends.forEach { handle ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "@$handle",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CirclePurple,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Comments Preview / Trigger
            if (post.comments.isNotEmpty()) {
                Text(
                    text = "View ${if (post.comments.size == 1) "1 comment" else "all ${post.comments.size} comments"}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .clickable { onCommentClick() }
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("view_comments_${post.id}")
                )
            }
        }
    }
}

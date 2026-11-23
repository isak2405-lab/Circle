package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.ReactionType
import com.example.ui.CircleViewModel
import com.example.ui.theme.OrbynTheme

@Composable
fun StoryViewerScreen(
    initialIndex: Int,
    viewModel: CircleViewModel,
    onClose: () -> Unit
) {
    BackHandler { onClose() }

    val colors = OrbynTheme.colors
    val colorScheme = MaterialTheme.colorScheme

    val stories by viewModel.stories.collectAsStateWithLifecycle()
    var currentStoryIndex by remember { mutableIntStateOf(initialIndex.coerceIn(0, (stories.size - 1).coerceAtLeast(0))) }
    var currentSlideIndex by remember { mutableIntStateOf(0) }
    var replyText by remember { mutableStateOf("") }
    var storyReactionEmoji by remember { mutableStateOf<String?>(null) }

    val currentStory = stories.getOrNull(currentStoryIndex)
    if (currentStory == null) {
        LaunchedEffect(Unit) { onClose() }
        return
    }

    val slides = currentStory.slides
    val currentSlide = slides.getOrNull(currentSlideIndex) ?: slides[0]

    LaunchedEffect(currentStory.user.id) {
        viewModel.markStorySeen(currentStory.user.id)
    }

    val progressAnim = remember(currentStoryIndex, currentSlideIndex) { Animatable(0f) }

    LaunchedEffect(currentStoryIndex, currentSlideIndex) {
        progressAnim.snapTo(0f)
        progressAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 5000, easing = LinearEasing)
        )
        if (currentSlideIndex < slides.size - 1) {
            currentSlideIndex++
        } else if (currentStoryIndex < stories.size - 1) {
            currentStoryIndex++
            currentSlideIndex = 0
        } else {
            onClose()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.mediaScrim)
            .testTag("story_viewer_screen")
    ) {
        AsyncImage(
            model = currentSlide.mediaUrl,
            contentDescription = currentSlide.caption,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colors.mediaScrim.copy(alpha = 0.65f),
                            Color.Transparent,
                            colors.mediaScrim.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(currentStoryIndex, currentSlideIndex) {
                    detectTapGestures(
                        onTap = { offset ->
                            val screenWidth = size.width
                            if (offset.x < screenWidth * 0.35f) {
                                if (currentSlideIndex > 0) {
                                    currentSlideIndex--
                                } else if (currentStoryIndex > 0) {
                                    currentStoryIndex--
                                    currentSlideIndex = 0
                                }
                            } else {
                                if (currentSlideIndex < slides.size - 1) {
                                    currentSlideIndex++
                                } else if (currentStoryIndex < stories.size - 1) {
                                    currentStoryIndex++
                                    currentSlideIndex = 0
                                } else {
                                    onClose()
                                }
                            }
                        }
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                slides.forEachIndexed { idx, _ ->
                    val fillFraction = when {
                        idx < currentSlideIndex -> 1f
                        idx == currentSlideIndex -> progressAnim.value
                        else -> 0f
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.5.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(colors.onMediaScrim.copy(alpha = 0.35f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fillFraction)
                                .fillMaxHeight()
                                .background(colors.onMediaScrim)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = currentStory.user.avatarUrl,
                    contentDescription = currentStory.user.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentStory.user.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = colors.onMediaScrim,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentSlide.timestamp,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = colors.onMediaScrim.copy(alpha = 0.7f)
                            )
                        )
                    }

                    if (currentStory.isCloseFriendsOnly) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(colors.closeFriendsAccent)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Close Friends Only",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = colors.closeFriendsAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("close_story_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = colors.onMediaScrim,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        if (currentSlide.caption.isNotBlank()) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp)
                    .background(
                        colors.mediaScrim.copy(alpha = 0.5f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = currentSlide.caption,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = colors.onMediaScrim,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        if (storyReactionEmoji != null) {
            Box(
                modifier = Modifier.align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = storyReactionEmoji ?: "",
                    fontSize = 72.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(ReactionType.HEART, ReactionType.LAUGH, ReactionType.FIRE).forEach { reaction ->
                    Surface(
                        shape = CircleShape,
                        color = colors.mediaScrim.copy(alpha = 0.5f),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable {
                                storyReactionEmoji = reaction.emoji
                                val conv = viewModel.conversations.value.find { it.friend.id == currentStory.user.id }
                                if (conv != null) {
                                    viewModel.sendMessage(conv.id, "Reacted ${reaction.emoji} to your story")
                                }
                            }
                    ) {
                        Text(
                            text = reaction.emoji,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = {
                        Text(
                            "Reply to ${currentStory.user.name.split(" ")[0]}...",
                            color = colors.onMediaScrim.copy(alpha = 0.65f),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(26.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.onMediaScrim,
                        unfocusedTextColor = colors.onMediaScrim,
                        focusedBorderColor = colors.onMediaScrim,
                        unfocusedBorderColor = colors.onMediaScrim.copy(alpha = 0.5f),
                        cursorColor = colorScheme.primary
                    ),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (replyText.isNotBlank()) {
                            val conv = viewModel.conversations.value.find { it.friend.id == currentStory.user.id }
                            if (conv != null) {
                                viewModel.sendMessage(conv.id, "Replying to story: $replyText")
                            }
                            replyText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (replyText.isNotBlank()) colorScheme.primary else colors.onMediaScrim.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send reply",
                        tint = if (replyText.isNotBlank()) colorScheme.onPrimary else colors.onMediaScrim,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

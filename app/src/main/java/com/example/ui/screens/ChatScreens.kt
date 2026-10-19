package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.ui.CircleViewModel
import com.example.ui.theme.CirclePurple
import com.example.ui.theme.CloseFriendsGreen
import com.example.ui.theme.PureWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    viewModel: CircleViewModel,
    onBack: () -> Unit,
    onOpenChat: (String) -> Unit
) {
    BackHandler { onBack() }

    val conversations by viewModel.conversations.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Messages",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("chat_list_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("chat_conversations_list")
        ) {
            items(conversations, key = { it.id }) { conv ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenChat(conv.id) }
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .testTag("conversation_item_${conv.friend.username}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Friend Avatar with Online Dot
                    Box(modifier = Modifier.size(54.dp)) {
                        AsyncImage(
                            model = conv.friend.avatarUrl,
                            contentDescription = conv.friend.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                        if (conv.friend.isOnline) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(CloseFriendsGreen)
                                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                    .align(Alignment.BottomEnd)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = conv.friend.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = if (conv.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = conv.timestamp,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = conv.lastMessage,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (conv.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal
                                ),
                                color = if (conv.unreadCount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            if (conv.unreadCount > 0) {
                                Surface(
                                    shape = CircleShape,
                                    color = CirclePurple,
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(
                                        text = conv.unreadCount.toString(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = PureWhite,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(start = 88.dp, end = 20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    conversationId: String,
    viewModel: CircleViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val conversations by viewModel.conversations.collectAsState()
    val playingVoiceId by viewModel.playingVoiceNoteId.collectAsState()
    val conv = conversations.find { it.id == conversationId }

    if (conv == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    var messageInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp)) {
                            AsyncImage(
                                model = conv.friend.avatarUrl,
                                contentDescription = conv.friend.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                            if (conv.friend.isOnline) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                    .background(CloseFriendsGreen)
                                    .align(Alignment.BottomEnd)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = conv.friend.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (conv.friend.isOnline) "Active in circle" else "Offline",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (conv.friend.isOnline) CloseFriendsGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("chat_detail_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Audio Call Button
                    IconButton(
                        onClick = { viewModel.startCall(conv.friend, com.example.data.model.CallType.VOICE) },
                        modifier = Modifier.testTag("voice_call_button_${conv.friend.username}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Voice Call",
                            tint = CirclePurple
                        )
                    }

                    // Video Call Button
                    IconButton(
                        onClick = { viewModel.startCall(conv.friend, com.example.data.model.CallType.VIDEO) },
                        modifier = Modifier.testTag("video_call_button_${conv.friend.username}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video Call",
                            tint = CirclePurple
                        )
                    }

                    // Security E2EE Details
                    IconButton(
                        onClick = { viewModel.navigateTo(com.example.ui.Screen.SecurityVault) },
                        modifier = Modifier.testTag("chat_security_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Security Vault",
                            tint = CloseFriendsGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Messages thread
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(conv.messages, key = { it.id }) { msg ->
                    val isMe = msg.senderId == "u_me"
                    ChatBubble(
                        message = msg,
                        isMe = isMe,
                        isPlayingVoice = playingVoiceId == msg.id,
                        onPlayVoice = { viewModel.togglePlayVoiceNote(msg.id) }
                    )
                }
            }

            // Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Send Voice Note button
                    IconButton(
                        onClick = {
                            viewModel.sendMessage(conversationId, null, isVoiceNote = true, voiceDuration = 18)
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("voice_note_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Send voice note",
                            tint = CirclePurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Text Field
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = { Text("Message ${conv.friend.name.split(" ")[0]}...", fontSize = 14.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_message_input"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CirclePurple
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Send Button
                    IconButton(
                        onClick = {
                            if (messageInput.isNotBlank()) {
                                viewModel.sendMessage(conversationId, messageInput)
                                messageInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (messageInput.isNotBlank()) CirclePurple else MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("send_chat_message_button"),
                        enabled = messageInput.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send message",
                            tint = if (messageInput.isNotBlank()) PureWhite else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    isMe: Boolean,
    isPlayingVoice: Boolean,
    onPlayVoice: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isMe) 18.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 18.dp
            ),
            color = if (isMe) CirclePurple else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                // Media preview if any
                if (message.mediaUrl != null) {
                    AsyncImage(
                        model = message.mediaUrl,
                        contentDescription = "Media",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Voice Note Player with animated waveform
                if (message.isVoiceNote) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onPlayVoice() }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isMe) PureWhite else CirclePurple,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPlayingVoice) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play voice note",
                                    tint = if (isMe) CirclePurple else PureWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Waveform bars
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            val barHeights = listOf(14, 22, 10, 26, 18, 12, 28, 16, 20, 10, 24, 18)
                            barHeights.forEachIndexed { i, height ->
                                val barColor = if (isMe) PureWhite else MaterialTheme.colorScheme.onSurface
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(if (isPlayingVoice && i % 2 == 0) (height * 1.3f).dp else height.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(barColor.copy(alpha = if (isPlayingVoice) 1f else 0.7f))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "0:${message.voiceDurationSec.toString().padStart(2, '0')}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isMe) PureWhite.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                } else if (message.text != null) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isMe) PureWhite else MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Time and Read receipts
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text(
                text = message.timestamp,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            if (isMe) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (message.isRead) "✓✓ Read" else "✓ Delivered",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        color = if (message.isRead) CirclePurple else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.ChatMessage
import com.example.ui.CircleViewModel
import com.example.ui.Screen
import com.example.ui.theme.OrbynTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    viewModel: CircleViewModel,
    onBack: () -> Unit,
    onOpenChat: (String) -> Unit
) {
    BackHandler { onBack() }

    val colors = OrbynTheme.colors
    val colorScheme = MaterialTheme.colorScheme
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = colors.canvas,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Messages",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.primaryText
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("chat_list_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.primaryText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.cardSurface
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
                                    .background(colors.closeFriendsAccent)
                                    .border(2.dp, colors.cardSurface, CircleShape)
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
                                color = colors.primaryText
                            )
                            Text(
                                text = conv.timestamp,
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.secondaryText
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
                                color = if (conv.unreadCount > 0) colors.primaryText else colors.secondaryText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            if (conv.unreadCount > 0) {
                                Surface(
                                    shape = CircleShape,
                                    color = colorScheme.primary,
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(
                                        text = conv.unreadCount.toString(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = colorScheme.onPrimary,
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
                    color = colors.divider,
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

    val colors = OrbynTheme.colors
    val colorScheme = MaterialTheme.colorScheme

    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val playingVoiceId by viewModel.playingVoiceNoteId.collectAsStateWithLifecycle()
    val conv = conversations.find { it.id == conversationId }

    if (conv == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    var messageInput by remember { mutableStateOf("") }
    var chatWarning by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = colors.canvas,
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
                                        .background(colors.closeFriendsAccent)
                                        .align(Alignment.BottomEnd)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = conv.friend.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = colors.primaryText
                            )
                            Text(
                                text = if (conv.friend.isOnline) "Active in circle" else "Offline",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (conv.friend.isOnline) colors.closeFriendsAccent else colors.secondaryText
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("chat_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.primaryText
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.SecurityVault) },
                        modifier = Modifier.testTag("chat_security_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Security Vault",
                            tint = colors.closeFriendsAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.cardSurface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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

            if (chatWarning != null) {
                Surface(
                    color = colors.warningContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = chatWarning ?: "",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = colors.onWarningContainer,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Input Bar
            Surface(
                color = colors.cardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                var showVoiceRecorder by remember { mutableStateOf(false) }
                var showStickerPicker by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showVoiceRecorder = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.elevatedSurface)
                            .testTag("voice_note_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Send voice note",
                            tint = colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { showStickerPicker = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.elevatedSurface)
                            .testTag("sticker_picker_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.StickyNote2,
                            contentDescription = "Send sticker",
                            tint = colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = {
                            Text(
                                "Message ${conv.friend.name.split(" ")[0]}...",
                                fontSize = 14.sp,
                                color = colors.placeholderText
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_message_input"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colors.primaryText,
                            unfocusedTextColor = colors.primaryText,
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colors.divider,
                            cursorColor = colorScheme.primary,
                            focusedContainerColor = colors.cardSurface,
                            unfocusedContainerColor = colors.cardSurface
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = colors.primaryText
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (messageInput.isNotBlank()) {
                                val screening = com.example.util.SafetyScreeningEngine.screenText(messageInput)
                                if (!screening.isSafe) {
                                    chatWarning = screening.message ?: "Message blocked by Safety Guard."
                                    return@IconButton
                                }
                                viewModel.sendMessage(conversationId, messageInput)
                                messageInput = ""
                                chatWarning = null
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (messageInput.isNotBlank()) colorScheme.primary else colors.elevatedSurface)
                            .testTag("send_chat_message_button"),
                        enabled = messageInput.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send message",
                            tint = if (messageInput.isNotBlank()) colorScheme.onPrimary else colors.secondaryText.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (showVoiceRecorder) {
                    AlertDialog(
                        onDismissRequest = { showVoiceRecorder = false },
                        containerColor = colors.cardSurface,
                        icon = { Icon(Icons.Default.Mic, contentDescription = null, tint = colorScheme.primary) },
                        title = { Text("Record Voice Message", color = colors.primaryText) },
                        text = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Recording secure voice note...", style = MaterialTheme.typography.bodyMedium, color = colors.secondaryText)
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val sampleHeights = listOf(16, 32, 20, 38, 28, 42, 24, 34, 18, 30)
                                    sampleHeights.forEach { h ->
                                        Box(
                                            modifier = Modifier
                                                .width(4.dp)
                                                .height(h.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(colorScheme.primary)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("0:12 / 1:00", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = colorScheme.primary)
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showVoiceRecorder = false
                                    viewModel.sendMessage(conversationId, null, isVoiceNote = true, voiceDuration = 12)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorScheme.primary,
                                    contentColor = colorScheme.onPrimary
                                )
                            ) {
                                Text("Send Voice Note")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showVoiceRecorder = false }) {
                                Text("Cancel", color = colors.secondaryText)
                            }
                        }
                    )
                }

                if (showStickerPicker) {
                    AlertDialog(
                        onDismissRequest = { showStickerPicker = false },
                        containerColor = colors.cardSurface,
                        title = { Text("Choose a Sticker", color = colors.primaryText) },
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 280.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val stickers = listOf(
                                    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80" to "Coffee Club",
                                    "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&auto=format&fit=crop&q=80" to "Smile Wave",
                                    "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=400&auto=format&fit=crop&q=80" to "Studio Vibes",
                                    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80" to "Adventure On"
                                )
                                stickers.forEach { (url, label) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                showStickerPicker = false
                                                viewModel.sendMessage(conversationId, "Sticker: $label", mediaUrl = url)
                                            }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = url,
                                            contentDescription = label,
                                            modifier = Modifier.size(54.dp).clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(label, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = colors.primaryText)
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showStickerPicker = false }) {
                                Text("Close", color = colorScheme.primary)
                            }
                        }
                    )
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
    val colors = OrbynTheme.colors
    val colorScheme = MaterialTheme.colorScheme

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
            color = if (isMe) colorScheme.primary else colors.elevatedSurface,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
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

                if (message.isVoiceNote) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onPlayVoice() }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isMe) colorScheme.onPrimary else colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPlayingVoice) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play voice note",
                                    tint = if (isMe) colorScheme.primary else colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            val barHeights = listOf(14, 22, 10, 26, 18, 12, 28, 16, 20, 10, 24, 18)
                            barHeights.forEachIndexed { i, height ->
                                val barColor = if (isMe) colorScheme.onPrimary else colors.primaryText
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
                                color = if (isMe) colorScheme.onPrimary.copy(alpha = 0.85f) else colors.secondaryText
                            )
                        )
                    }
                } else if (message.text != null) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isMe) colorScheme.onPrimary else colors.primaryText
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text(
                text = message.timestamp,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = colors.secondaryText
                )
            )
            if (isMe) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (message.isRead) "✓✓ Read" else "✓ Delivered",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        color = if (message.isRead) colorScheme.primary else colors.secondaryText
                    )
                )
            }
        }
    }
}

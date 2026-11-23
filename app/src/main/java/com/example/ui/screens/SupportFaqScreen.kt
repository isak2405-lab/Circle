package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FaqItem
import com.example.data.model.SupportMessage
import com.example.ui.CircleViewModel
import com.example.ui.Screen
import com.example.ui.theme.OrbynTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportFaqScreen(
    viewModel: CircleViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val colors = OrbynTheme.colors
    val colorScheme = MaterialTheme.colorScheme

    val messages by viewModel.supportMessages.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Chatbot, 1: FAQ Directory
    var inputQuery by remember { mutableStateOf("") }
    var faqSearchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = colors.canvas,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "FAQ & Orbit Support Bot",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = colors.primaryText
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(colors.closeFriendsAccent)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Automated Privacy Assistant • Instant Answers",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = colors.secondaryText
                                    )
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("support_back_button")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = colors.cardSurface,
                contentColor = colorScheme.primary,
                divider = { HorizontalDivider(color = colors.divider) }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    selectedContentColor = colorScheme.primary,
                    unselectedContentColor = colors.secondaryText,
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Orbit Assistant Bot", fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    selectedContentColor = colorScheme.primary,
                    unselectedContentColor = colors.secondaryText,
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HelpCenter, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Browse FAQ Knowledge Base", fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
            }

            if (selectedTab == 0) {
                Column(modifier = Modifier.fillMaxSize()) {
                    val quickTopics = listOf(
                        "Why can't my family download on Play Store?" to "Why can't my wife or friends download the app on Google Play due to age restriction?",
                        "Strict Invite-Only Circle" to "Can interactions in the app be limited to invited friends only?",
                        "Military-Grade E2EE" to "How does End-to-End Encryption and Safety Keys work?",
                        "On-Device Nudity & Abuse Filter" to "How does nudity and abuse screening work?",
                        "Profile Picture & Photo Picker" to "How do I update my profile picture from gallery?",
                        "Encrypted Peer Calling" to "How does encrypted voice and video calling work?"
                    )

                    Surface(
                        color = colors.cardSurface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                            Text(
                                text = "POPULAR TOPICS (TAP TO ASK):",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.secondaryText,
                                    fontSize = 10.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(quickTopics) { (label, fullQuery) ->
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = colors.selectedContainer,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider),
                                        modifier = Modifier.clickable {
                                            viewModel.sendSupportQuery(fullQuery)
                                        }
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = colors.onSelectedContainer,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = colors.divider)

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            SupportMessageBubble(
                                message = msg,
                                onActionClick = { actionType ->
                                    when (actionType) {
                                        "SECURITY_VAULT" -> viewModel.navigateTo(Screen.SecurityVault)
                                        "PROFILE" -> viewModel.navigateTo(Screen.Profile)
                                        "FAQ" -> selectedTab = 1
                                    }
                                }
                            )
                        }
                    }

                    Surface(
                        color = colors.cardSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider),
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
                            OutlinedTextField(
                                value = inputQuery,
                                onValueChange = { inputQuery = it },
                                placeholder = {
                                    Text(
                                        "Ask anything (e.g. 'Play Store download', 'invite only')...",
                                        fontSize = 13.sp,
                                        color = colors.placeholderText
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("support_input_field"),
                                shape = RoundedCornerShape(22.dp),
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
                                maxLines = 3
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = {
                                    if (inputQuery.isNotBlank()) {
                                        viewModel.sendSupportQuery(inputQuery)
                                        inputQuery = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (inputQuery.isNotBlank()) colorScheme.primary else colors.elevatedSurface)
                                    .testTag("support_send_button"),
                                enabled = inputQuery.isNotBlank()
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = if (inputQuery.isNotBlank()) colorScheme.onPrimary else colors.secondaryText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                val staticFaqs = listOf(
                    FaqItem(
                        id = "faq_play_age",
                        category = "Google Play & Downloads",
                        question = "Why does the app show an age restriction or fail to download on family phones?",
                        answer = "Google Play accounts with parental controls or Family Link block apps with an 18+ rating. To fix this:\n1. Log in to Google Play Console > Policy > App content > Content ratings.\n2. Retake questionnaire: mark 'No' to explicit nudity, sexual content, and violence.\n3. This drops the rating to PEGI 12 / Teen / Everyone 10+.\n4. Under Target Audience, select ages 13-15, 16-17, and 18+.\n5. If using an Internal/Closed testing track, ensure the tester's email is added in Google Play Console Testers list and they accept the web invite link."
                    ),
                    FaqItem(
                        id = "faq_invite_only",
                        category = "Privacy & Close Friends",
                        question = "Can interactions in the app be limited to invited friends only?",
                        answer = "Yes! Circle includes a 'Strict Invite-Only Circle' security control in the Security Vault. When active, only users who are in your mutual close friends list can view your moments, send DMs, or initiate calls. Your account is completely hidden from public search."
                    ),
                    FaqItem(
                        id = "faq_e2ee",
                        category = "Security & Encryption",
                        question = "What cryptography does Circle use to protect my data?",
                        answer = "Circle uses Curve25519 for client-side key exchange, combined with ChaCha20-Poly1305 and AES-256-GCM hardware-backed keystore encryption. You can view and rotate your safety fingerprint in the Security Vault anytime."
                    ),
                    FaqItem(
                        id = "faq_nudity_abuse",
                        category = "Content Moderation & Safety",
                        question = "How does real-time nudity and abuse screening work?",
                        answer = "Screening runs locally on your device prior to sharing. It uses normalized lexical heuristics to block severe abuse, threats, and harassment, plus a local skin-tone chromatic ratio heuristic for photos. No unencrypted content is uploaded to external AI surveillance servers."
                    ),
                    FaqItem(
                        id = "faq_avatar",
                        category = "Profile & Account",
                        question = "How do I update my profile picture while editing?",
                        answer = "Go to your Profile tab and tap directly on your avatar (or tap 'Account & Profile' in the Settings tab). Tapping your photo opens the Android Photo Picker where you can select any photo from your gallery, or choose from the aesthetic presets tray."
                    ),
                    FaqItem(
                        id = "faq_calls",
                        category = "Calling",
                        question = "Are voice and video calls encrypted and private?",
                        answer = "Yes. Calls are secured with peer-to-peer encryption and display a verified cryptographic fingerprint. You can also turn on 'Encrypted Peer-Relay Calling' in the Security Vault to prevent exposing your IP address to callers."
                    )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    OutlinedTextField(
                        value = faqSearchQuery,
                        onValueChange = { faqSearchQuery = it },
                        placeholder = { Text("Search FAQ topics (e.g. 'download', 'photo')...", color = colors.placeholderText) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = colors.secondaryText) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("faq_search_input"),
                        shape = RoundedCornerShape(14.dp),
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
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val filteredFaqs = remember(faqSearchQuery) {
                        if (faqSearchQuery.isBlank()) staticFaqs
                        else staticFaqs.filter {
                            it.question.contains(faqSearchQuery, ignoreCase = true) ||
                                    it.answer.contains(faqSearchQuery, ignoreCase = true) ||
                                    it.category.contains(faqSearchQuery, ignoreCase = true)
                        }
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredFaqs, key = { it.id }) { faq ->
                            FaqExpandableCard(faq = faq)
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = colors.selectedContainer),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedTab = 0 }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Chat,
                                        contentDescription = null,
                                        tint = colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Didn't find your answer? Chat live with Orbit Support Bot ➔",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = colors.onSelectedContainer,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SupportMessageBubble(
    message: SupportMessage,
    onActionClick: (String) -> Unit
) {
    val colors = OrbynTheme.colors
    val colorScheme = MaterialTheme.colorScheme
    val isBot = message.isFromBot

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isBot) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        if (isBot) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "Bot",
                    tint = colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 300.dp),
            horizontalAlignment = if (isBot) Alignment.Start else Alignment.End
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isBot) 4.dp else 16.dp,
                    bottomEnd = if (isBot) 16.dp else 4.dp
                ),
                color = if (isBot) colors.cardSurface else colorScheme.primary,
                border = if (isBot) androidx.compose.foundation.BorderStroke(1.dp, colors.divider) else null,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isBot) colors.primaryText else colorScheme.onPrimary,
                            lineHeight = 20.sp
                        )
                    )

                    if (message.actionLabel != null && message.actionType != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onActionClick(message.actionType) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary,
                                contentColor = colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = message.actionLabel,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FaqExpandableCard(faq: FaqItem) {
    val colors = OrbynTheme.colors
    val colorScheme = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.cardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = faq.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = faq.question,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = colors.primaryText
                        )
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = colors.secondaryText,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = colors.divider)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = faq.answer,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.secondaryText,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }
    }
}

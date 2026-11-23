package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.PostType
import com.example.ui.CircleViewModel
import com.example.ui.theme.OrbynTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    viewModel: CircleViewModel,
    onBack: () -> Unit
) {
    val colors = OrbynTheme.colors
    val colorScheme = MaterialTheme.colorScheme

    val aestheticFilters = viewModel.aestheticFilters
    val postDraft by viewModel.postDraft.collectAsStateWithLifecycle()
    val friends = remember { viewModel.getFriends() }

    val samplePhotos = listOf(
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=900&auto=format&fit=crop&q=80"
    )

    var selectedType by remember { mutableStateOf(postDraft?.type ?: PostType.PHOTO) }
    var caption by remember { mutableStateOf(postDraft?.caption ?: "") }
    var thoughtText by remember { mutableStateOf(postDraft?.thoughtText ?: "") }
    var selectedFilter by remember { mutableStateOf(postDraft?.filterName ?: "Original") }
    var location by remember { mutableStateOf(postDraft?.location ?: "") }
    var isCloseFriendsOnly by remember { mutableStateOf(postDraft?.isCloseFriendsOnly ?: true) }
    var taggedFriends by remember { mutableStateOf<List<String>>(emptyList()) }
    var draftSavedNotice by remember { mutableStateOf(false) }
    var selectedMediaUrl by remember { mutableStateOf(postDraft?.mediaUrl ?: samplePhotos[0]) }
    var safetyAlertMessage by remember { mutableStateOf<String?>(null) }

    BackHandler {
        if (caption.isNotBlank() || thoughtText.isNotBlank()) {
            viewModel.saveDraft(
                caption = caption,
                type = selectedType,
                mediaUrl = selectedMediaUrl,
                thoughtText = thoughtText,
                filterName = selectedFilter,
                location = location,
                isCloseFriendsOnly = isCloseFriendsOnly
            )
        }
        onBack()
    }

    Scaffold(
        containerColor = colors.canvas,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Moment",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.primaryText
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (caption.isNotBlank() || thoughtText.isNotBlank()) {
                                viewModel.saveDraft(
                                    caption = caption,
                                    type = selectedType,
                                    mediaUrl = selectedMediaUrl,
                                    thoughtText = thoughtText,
                                    filterName = selectedFilter,
                                    location = location,
                                    isCloseFriendsOnly = isCloseFriendsOnly
                                )
                            }
                            onBack()
                        },
                        modifier = Modifier.testTag("create_post_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel", tint = colors.primaryText)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.saveDraft(
                                caption = caption,
                                type = selectedType,
                                mediaUrl = selectedMediaUrl,
                                thoughtText = thoughtText,
                                filterName = selectedFilter,
                                location = location,
                                isCloseFriendsOnly = isCloseFriendsOnly
                            )
                            draftSavedNotice = true
                        },
                        modifier = Modifier.testTag("save_draft_button")
                    ) {
                        Text(
                            text = if (draftSavedNotice) "Saved ✓" else "Save for later",
                            color = colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Button(
                        onClick = {
                            val screening = com.example.util.SafetyScreeningEngine.screenPost(
                                caption = caption,
                                thoughtText = if (selectedType == PostType.THOUGHT) thoughtText else null,
                                mediaUrl = if (selectedType == PostType.THOUGHT) null else selectedMediaUrl
                            )
                            if (!screening.isSafe) {
                                safetyAlertMessage = screening.message ?: "Content flagged by Safety Guard."
                                return@Button
                            }

                            viewModel.createPost(
                                type = selectedType,
                                mediaUrl = if (selectedType == PostType.THOUGHT) null else selectedMediaUrl,
                                thoughtText = if (selectedType == PostType.THOUGHT) thoughtText else null,
                                filterName = selectedFilter,
                                caption = caption,
                                location = location,
                                taggedFriends = taggedFriends,
                                isCloseFriendsOnly = isCloseFriendsOnly
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("publish_post_button"),
                        enabled = (selectedType != PostType.THOUGHT && selectedMediaUrl.isNotBlank()) ||
                                (selectedType == PostType.THOUGHT && thoughtText.isNotBlank()) ||
                                caption.isNotBlank()
                    ) {
                        Text("Share", fontWeight = FontWeight.SemiBold)
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Type Selector: Photo, Video, Thought
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colors.elevatedSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PostType.entries.forEach { type ->
                        val isSelected = selectedType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) colors.cardSurface else Color.Transparent)
                                .clickable { selectedType = type }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (type) {
                                    PostType.PHOTO -> "Photo"
                                    PostType.VIDEO -> "Video (60s)"
                                    PostType.THOUGHT -> "Thought"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) colors.primaryText else colors.secondaryText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Media or Thought Input
            if (selectedType == PostType.THOUGHT) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.primary)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        TextField(
                            value = thoughtText,
                            onValueChange = { thoughtText = it },
                            placeholder = {
                                Text(
                                    "What's on your mind? Share an unfiltered thought with your circle...",
                                    color = colorScheme.onPrimary.copy(alpha = 0.7f),
                                    fontSize = 16.sp
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = colorScheme.onPrimary,
                                unfocusedTextColor = colorScheme.onPrimary,
                                cursorColor = colorScheme.onPrimary,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("thought_input_field")
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.2f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.elevatedSurface),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = selectedMediaUrl,
                        contentDescription = "Selected media",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (selectedType == PostType.VIDEO) {
                        Surface(
                            shape = CircleShape,
                            color = colors.mediaScrim.copy(alpha = 0.6f),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Video",
                                    tint = colors.onMediaScrim,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    // Filter Label Chip
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colors.mediaScrim.copy(alpha = 0.6f),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Filter: $selectedFilter",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = colors.onMediaScrim,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Select Moment",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.secondaryText
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(samplePhotos) { photoUrl ->
                        val isPicked = selectedMediaUrl == photoUrl
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isPicked) 2.5.dp else 0.dp,
                                    color = if (isPicked) colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedMediaUrl = photoUrl }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Aesthetic Filter (5 Styles)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.secondaryText
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(aestheticFilters) { filter ->
                        val isSelected = selectedFilter == filter.name
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) colorScheme.primary else colors.elevatedSurface,
                            modifier = Modifier
                                .clickable { selectedFilter = filter.name }
                                .testTag("filter_${filter.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = filter.name,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) colorScheme.onPrimary else colors.primaryText
                                    )
                                )
                                Text(
                                    text = filter.description,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = if (isSelected) colorScheme.onPrimary.copy(alpha = 0.8f) else colors.secondaryText
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            val postFieldColors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colors.primaryText,
                unfocusedTextColor = colors.primaryText,
                focusedLabelColor = colorScheme.primary,
                unfocusedLabelColor = colors.secondaryText,
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colors.divider,
                cursorColor = colorScheme.primary,
                focusedPlaceholderColor = colors.placeholderText,
                unfocusedPlaceholderColor = colors.placeholderText,
                focusedContainerColor = colors.cardSurface,
                unfocusedContainerColor = colors.cardSurface
            )
            val postFieldTextStyle = MaterialTheme.typography.bodyMedium.copy(
                color = colors.primaryText
            )

            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Write a caption...") },
                placeholder = { Text("Unfiltered, for your people.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("caption_input_field"),
                shape = RoundedCornerShape(18.dp),
                minLines = 2,
                colors = postFieldColors,
                textStyle = postFieldTextStyle
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Add Location") },
                placeholder = { Text("e.g. Café Kitsuné, West Village") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = colorScheme.primary)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("location_input_field"),
                shape = RoundedCornerShape(18.dp),
                singleLine = true,
                colors = postFieldColors,
                textStyle = postFieldTextStyle
            )

            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("West Village", "Studio B", "Brooklyn Rooftop", "Home Sweet Home", "Late Night Walk")) { place ->
                    SuggestionChip(
                        onClick = { location = place },
                        label = { Text(place, fontSize = 11.sp, color = colors.primaryText) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = colors.cardSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tag Friends in Circle",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.secondaryText
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(friends) { friend ->
                    val isTagged = taggedFriends.contains(friend.username)
                    FilterChip(
                        selected = isTagged,
                        onClick = {
                            taggedFriends = if (isTagged) {
                                taggedFriends - friend.username
                            } else {
                                taggedFriends + friend.username
                            }
                        },
                        label = { Text("@${friend.username}") },
                        leadingIcon = {
                            AsyncImage(
                                model = friend.avatarUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Close Friends Only Switch
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = if (isCloseFriendsOnly) colors.closeFriendsContainer else colors.elevatedSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(colors.closeFriendsAccent)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Close Friends Only",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isCloseFriendsOnly) colors.closeFriendsAccent else colors.primaryText
                            )
                            Text(
                                text = "Only your designated inner circle will see this moment.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.secondaryText
                            )
                        }
                    }

                    Switch(
                        checked = isCloseFriendsOnly,
                        onCheckedChange = { isCloseFriendsOnly = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.onCloseFriendsAccent,
                            checkedTrackColor = colors.closeFriendsAccent
                        ),
                        modifier = Modifier.testTag("close_friends_toggle_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (safetyAlertMessage != null) {
        AlertDialog(
            onDismissRequest = { safetyAlertMessage = null },
            containerColor = colors.cardSurface,
            icon = {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Safety Alert",
                    tint = colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "ORBYN Safety Guard",
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryText
                )
            },
            text = {
                Text(
                    text = safetyAlertMessage ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.primaryText
                )
            },
            confirmButton = {
                Button(
                    onClick = { safetyAlertMessage = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Understood")
                }
            }
        )
    }
}

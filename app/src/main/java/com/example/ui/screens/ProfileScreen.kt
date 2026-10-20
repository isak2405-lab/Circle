package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import coil.compose.AsyncImage
import com.example.data.model.Post
import com.example.data.model.PostType
import com.example.data.model.User
import com.example.ui.CircleViewModel
import com.example.ui.Screen
import com.example.ui.theme.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: CircleViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val highlights = viewModel.repository.profileHighlights

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Grid, 1: Settings
    var showEditProfileDialog by remember { mutableStateOf(false) }

    val myPosts = remember(posts, currentUser.id) {
        posts.filter { it.author.id == currentUser.id }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(AppCanvas)
    ) {
        // Top Bar
        Surface(
            color = SurfaceColor,
            tonalElevation = 0.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, DividerOutline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "@${currentUser.username}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = PrimaryText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Private Circle",
                        tint = SecondaryText,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(com.example.ui.Screen.SecurityVault) },
                        modifier = Modifier.testTag("profile_security_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Security Vault",
                            tint = CloseFriendsGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sign Out",
                            tint = SecondaryText
                        )
                    }
                }
            }
        }

        // Header Profile Info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with edit affordance
            Box(
                modifier = Modifier
                    .size(78.dp)
                    .clickable { showEditProfileDialog = true }
                    .testTag("profile_avatar_image"),
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.5.dp, BrandPrimary, CircleShape)
                        .padding(3.dp)
                ) {
                    AsyncImage(
                        model = currentUser.avatarUrl,
                        contentDescription = currentUser.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(BrandPrimary)
                        .border(1.5.dp, SurfaceColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit photo",
                        tint = PureWhite,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Follower / Following counts: strictly owner-only visible
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ProfileStatItem(count = "${posts.size}", label = "Moments")
                ProfileStatItem(count = "14", label = "Close circle")
                ProfileStatItem(count = "28", label = "Following")
            }
        }

        // Name, Tagline & Bio
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = currentUser.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = PrimaryText
            )

            Text(
                text = "Your inner orbit.",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = BrandPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            if (currentUser.bio.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentUser.bio,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        color = PrimaryText
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Outline Edit Profile Button (10-12dp radius)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            OutlinedButton(
                onClick = { showEditProfileDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("edit_profile_button"),
                shape = RoundedCornerShape(11.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DividerOutline),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryText
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Edit profile",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Moments Highlights Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(highlights) { (title, imageUrl) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(62.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .border(1.dp, DividerOutline, CircleShape)
                            .padding(2.5.dp)
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = PrimaryText
                        ),
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tabs: Media Grid vs Simple White Settings sections
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceColor,
            contentColor = BrandPrimary,
            modifier = Modifier.height(42.dp)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Moments Grid", fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Settings", fontSize = 12.sp) }
            )
        }

        // Tab Content
        if (selectedTab == 0) {
            // Three-column rounded media grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(posts, key = { it.id }) { post ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DividerOutline),
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { }
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (post.type == PostType.THOUGHT) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(SelectedBackground)
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = post.thoughtText ?: "",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = PrimaryText,
                                            fontSize = 9.sp
                                        ),
                                        maxLines = 3
                                    )
                                }
                            } else {
                                AsyncImage(
                                    model = post.mediaUrl,
                                    contentDescription = post.caption,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Settings: Simple white sections for account, privacy and inner-circle membership
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SettingsSectionCard(
                    title = "Account & Profile",
                    description = "Edit name, handle, avatar and export your moments",
                    onClick = { showEditProfileDialog = true }
                )

                SettingsSectionCard(
                    title = "Privacy & Security Vault",
                    description = "E2EE Curve25519, screenshot shield & disappearing timers",
                    onClick = { viewModel.navigateTo(Screen.SecurityVault) }
                )

                val circleMembers by viewModel.circleMembers.collectAsStateWithLifecycle()
                var showInnerCircleManager by remember { mutableStateOf(false) }
                var showCircleFullDialog by remember { mutableStateOf(false) }

                SettingsSectionCard(
                    title = "Inner-Circle Membership (${circleMembers.size}/12)",
                    description = "Strict 12-person limit. Manage close friends and permissions.",
                    onClick = { showInnerCircleManager = true }
                )

                if (showInnerCircleManager) {
                    AlertDialog(
                        onDismissRequest = { showInnerCircleManager = false },
                        title = {
                            Column {
                                Text("Inner-Circle Membership", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { circleMembers.size / 12f },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = BrandPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${circleMembers.size} of 12 slots filled",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SecondaryText
                                )
                            }
                        },
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 300.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                circleMembers.forEach { member ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            AsyncImage(
                                                model = member.avatarUrl,
                                                contentDescription = null,
                                                modifier = Modifier.size(32.dp).clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(member.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                                Text("@${member.username}", style = MaterialTheme.typography.labelSmall, color = SecondaryText)
                                            }
                                        }
                                        TextButton(onClick = { viewModel.removeCircleMember(member.id) }) {
                                            Text("Remove", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (circleMembers.size >= 12) {
                                        showInnerCircleManager = false
                                        showCircleFullDialog = true
                                    } else {
                                        // Add sample new member
                                        val newId = "u_${System.currentTimeMillis()}"
                                        val newMember = User(
                                            id = newId,
                                            name = "New Friend",
                                            username = "friend_${circleMembers.size + 1}",
                                            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                                            bio = "Invited to the circle",
                                            isCloseFriend = true
                                        )
                                        val success = viewModel.addCircleMember(newMember)
                                        if (!success) {
                                            showInnerCircleManager = false
                                            showCircleFullDialog = true
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                            ) {
                                Text("Add Member")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showInnerCircleManager = false }) {
                                Text("Close")
                            }
                        }
                    )
                }

                if (showCircleFullDialog) {
                    AlertDialog(
                        onDismissRequest = { showCircleFullDialog = false },
                        icon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BrandPrimary) },
                        title = { Text("Circle Full (12/12)") },
                        text = { Text("Your Circle has reached the strict maximum limit of 12 close friends. To add a new member, please remove someone from your circle first.") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showCircleFullDialog = false
                                    showInnerCircleManager = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                            ) {
                                Text("Manage Circle")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showCircleFullDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Demo Mode • ORBYN • Less noise. Closer circle.",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            color = SecondaryText
                        )
                    )
                }
            }
        }
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            name = currentUser.name,
            username = currentUser.username,
            bio = currentUser.bio,
            status = currentUser.statusText ?: "",
            avatarUrl = currentUser.avatarUrl,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, username, bio, status, avatarUrl ->
                viewModel.updateProfile(name, username, bio, avatarUrl, status)
                showEditProfileDialog = false
            }
        )
    }
}

@Composable
private fun ProfileStatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = PrimaryText
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                color = SecondaryText
            )
        )
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SurfaceColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, DividerOutline),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = SecondaryText,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private val defaultAvatarPresets = listOf(
    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=400&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=400&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80"
)

@Composable
private fun EditProfileDialog(
    name: String,
    username: String,
    bio: String,
    status: String,
    avatarUrl: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var editName by remember { mutableStateOf(name) }
    var editUsername by remember { mutableStateOf(username) }
    var editBio by remember { mutableStateOf(bio) }
    var editStatus by remember { mutableStateOf(status) }
    var editAvatar by remember { mutableStateOf(avatarUrl) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            editAvatar = uri.toString()
        }
    }

    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedLabelColor = BrandPrimary,
        unfocusedLabelColor = SecondaryText,
        focusedBorderColor = BrandPrimary,
        unfocusedBorderColor = DividerOutline,
        cursorColor = BrandPrimary,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface
    )
    val inputTextStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Normal
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Edit profile",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture with live preview & pick button
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .testTag("edit_avatar_picker_button"),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(2.dp, BrandPrimary, CircleShape)
                            .padding(3.dp)
                    ) {
                        AsyncImage(
                            model = editAvatar,
                            contentDescription = "Edit Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = BrandPrimary,
                        border = androidx.compose.foundation.BorderStroke(2.dp, SurfaceColor),
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change photo",
                                tint = PureWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Pick from Gallery CTA
                TextButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.testTag("choose_gallery_photo_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Upload from Gallery",
                        color = BrandPrimary,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                // Avatar Presets Tray
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Or choose avatar preset:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SecondaryText,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(defaultAvatarPresets) { presetUrl ->
                            val isSelected = editAvatar == presetUrl
                            AsyncImage(
                                model = presetUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) BrandPrimary else DividerOutline,
                                        shape = CircleShape
                                    )
                                    .clickable { editAvatar = presetUrl }
                            )
                        }
                    }
                }

                HorizontalDivider(color = DividerOutline, modifier = Modifier.padding(vertical = 4.dp))

                // Name Field
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_name_input"),
                    colors = inputColors,
                    textStyle = inputTextStyle
                )

                // Username Field
                OutlinedTextField(
                    value = editUsername,
                    onValueChange = { editUsername = it },
                    label = { Text("Username") },
                    leadingIcon = {
                        Text(
                            "@",
                            fontWeight = FontWeight.Bold,
                            color = SecondaryText,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_username_input"),
                    colors = inputColors,
                    textStyle = inputTextStyle
                )

                // Status Field
                OutlinedTextField(
                    value = editStatus,
                    onValueChange = { editStatus = it },
                    label = { Text("Status text") },
                    placeholder = { Text("e.g. 🎧 listening to music", color = PlaceholderHint) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_status_input"),
                    colors = inputColors,
                    textStyle = inputTextStyle
                )

                // Bio Field
                OutlinedTextField(
                    value = editBio,
                    onValueChange = { editBio = it },
                    label = { Text("Bio") },
                    maxLines = 3,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_bio_input"),
                    colors = inputColors,
                    textStyle = inputTextStyle
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(editName, editUsername, editBio, editStatus, editAvatar) },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("edit_profile_save_button")
            ) {
                Text("Save", color = PureWhite, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("edit_profile_cancel_button")
            ) {
                Text("Cancel", color = SecondaryText)
            }
        }
    )
}

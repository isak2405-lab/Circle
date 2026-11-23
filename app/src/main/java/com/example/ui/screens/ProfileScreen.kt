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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.PostType
import com.example.data.model.User
import com.example.ui.CircleViewModel
import com.example.ui.Screen
import com.example.ui.theme.OrbynTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: CircleViewModel,
    modifier: Modifier = Modifier
) {
    val colors = OrbynTheme.colors
    val colorScheme = MaterialTheme.colorScheme

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val themeConfig by viewModel.themeConfig.collectAsStateWithLifecycle()
    val highlights = viewModel.repository.profileHighlights

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Grid, 1: Settings
    var showEditProfileDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(colors.canvas)
    ) {
        // Top Bar
        Surface(
            color = colors.cardSurface,
            tonalElevation = 0.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider),
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
                        color = colors.primaryText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Private Circle",
                        tint = colors.secondaryText,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.SecurityVault) },
                        modifier = Modifier.testTag("profile_security_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Security Vault",
                            tint = colors.closeFriendsAccent,
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
                            tint = colors.secondaryText
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
                        .border(1.5.dp, colorScheme.primary, CircleShape)
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
                        .background(colorScheme.primary)
                        .border(1.5.dp, colors.cardSurface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit photo",
                        tint = colorScheme.onPrimary,
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
                color = colors.primaryText
            )

            Text(
                text = "Your inner orbit.",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = colorScheme.primary,
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
                        color = colors.primaryText
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Outline Edit Profile Button
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
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = colors.cardSurface,
                    contentColor = colors.primaryText
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
                            .border(1.dp, colors.divider, CircleShape)
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
                            color = colors.primaryText
                        ),
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tabs: Media Grid vs Settings
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = colors.cardSurface,
            contentColor = colorScheme.primary,
            divider = { HorizontalDivider(color = colors.divider) },
            modifier = Modifier.height(44.dp)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                selectedContentColor = colorScheme.primary,
                unselectedContentColor = colors.secondaryText,
                modifier = Modifier.testTag("profile_tab_grid"),
                text = { Text("Moments Grid", fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                selectedContentColor = colorScheme.primary,
                unselectedContentColor = colors.secondaryText,
                modifier = Modifier.testTag("profile_tab_settings"),
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
                        colors = CardDefaults.cardColors(containerColor = colors.cardSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider),
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { }
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (post.type == PostType.THOUGHT) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(colors.selectedContainer)
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = post.thoughtText ?: "",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = colors.primaryText,
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
            // Settings: Appearance & Themes, Account, Privacy, Inner-Circle
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SettingsSectionCard(
                    title = "Appearance & Themes",
                    description = "${themeConfig.theme.displayName} • ${themeConfig.appearanceMode.displayName} mode",
                    leadingIcon = Icons.Default.Palette,
                    badgeText = themeConfig.theme.displayName,
                    testTag = "settings_appearance_themes_card",
                    onClick = { viewModel.navigateTo(Screen.AppearanceThemes) }
                )

                SettingsSectionCard(
                    title = "Account & Profile",
                    description = "Edit name, handle, avatar and export your moments",
                    leadingIcon = Icons.Default.Person,
                    testTag = "settings_account_card",
                    onClick = { showEditProfileDialog = true }
                )

                SettingsSectionCard(
                    title = "Privacy & Security Vault",
                    description = "E2EE Curve25519, screenshot shield & disappearing timers",
                    leadingIcon = Icons.Default.Shield,
                    testTag = "settings_security_card",
                    onClick = { viewModel.navigateTo(Screen.SecurityVault) }
                )

                val circleMembers by viewModel.circleMembers.collectAsStateWithLifecycle()
                var showInnerCircleManager by remember { mutableStateOf(false) }
                var showCircleFullDialog by remember { mutableStateOf(false) }

                SettingsSectionCard(
                    title = "Inner-Circle Membership (${circleMembers.size}/12)",
                    description = "Strict 12-person limit. Manage close friends and permissions.",
                    leadingIcon = Icons.Default.Groups,
                    testTag = "settings_inner_circle_card",
                    onClick = { showInnerCircleManager = true }
                )

                if (showInnerCircleManager) {
                    AlertDialog(
                        onDismissRequest = { showInnerCircleManager = false },
                        containerColor = colors.cardSurface,
                        titleContentColor = colors.primaryText,
                        textContentColor = colors.primaryText,
                        title = {
                            Column {
                                Text("Inner-Circle Membership", style = MaterialTheme.typography.titleMedium, color = colors.primaryText)
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { circleMembers.size / 12f },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = colorScheme.primary,
                                    trackColor = colors.selectedContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${circleMembers.size} of 12 slots filled",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.secondaryText
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
                                            .background(colors.elevatedSurface, RoundedCornerShape(10.dp))
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
                                                Text(member.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = colors.primaryText)
                                                Text("@${member.username}", style = MaterialTheme.typography.labelSmall, color = colors.secondaryText)
                                            }
                                        }
                                        TextButton(onClick = { viewModel.removeCircleMember(member.id) }) {
                                            Text("Remove", color = colorScheme.error, fontSize = 12.sp)
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
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorScheme.primary,
                                    contentColor = colorScheme.onPrimary
                                )
                            ) {
                                Text("Add Member")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showInnerCircleManager = false }) {
                                Text("Close", color = colors.secondaryText)
                            }
                        }
                    )
                }

                if (showCircleFullDialog) {
                    AlertDialog(
                        onDismissRequest = { showCircleFullDialog = false },
                        containerColor = colors.cardSurface,
                        icon = { Icon(Icons.Default.Lock, contentDescription = null, tint = colorScheme.primary) },
                        title = { Text("Circle Full (12/12)", color = colors.primaryText) },
                        text = {
                            Text(
                                "Your Circle has reached the strict maximum limit of 12 close friends. To add a new member, please remove someone from your circle first.",
                                color = colors.primaryText
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showCircleFullDialog = false
                                    showInnerCircleManager = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorScheme.primary,
                                    contentColor = colorScheme.onPrimary
                                )
                            ) {
                                Text("Manage Circle")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showCircleFullDialog = false }) {
                                Text("Cancel", color = colors.secondaryText)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ORBYN • Less noise. Closer circle.",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            color = colors.secondaryText
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
    val colors = OrbynTheme.colors
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = colors.primaryText
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                color = colors.secondaryText
            )
        )
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    description: String,
    leadingIcon: ImageVector? = null,
    badgeText: String? = null,
    testTag: String? = null,
    onClick: () -> Unit
) {
    val colors = OrbynTheme.colors
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.cardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.selectedContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    color = colors.primaryText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = colors.secondaryText
                    )
                )
            }

            if (badgeText != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colors.selectedContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = colors.onSelectedContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = colors.secondaryText,
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
    val colors = OrbynTheme.colors
    val colorScheme = MaterialTheme.colorScheme

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
        focusedTextColor = colors.primaryText,
        unfocusedTextColor = colors.primaryText,
        focusedLabelColor = colorScheme.primary,
        unfocusedLabelColor = colors.secondaryText,
        focusedBorderColor = colorScheme.primary,
        unfocusedBorderColor = colors.divider,
        cursorColor = colorScheme.primary,
        focusedContainerColor = colors.cardSurface,
        unfocusedContainerColor = colors.cardSurface
    )
    val inputTextStyle = MaterialTheme.typography.bodyMedium.copy(
        color = colors.primaryText,
        fontWeight = FontWeight.Normal
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.cardSurface,
        title = {
            Text(
                "Edit profile",
                fontWeight = FontWeight.Bold,
                color = colors.primaryText
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
                            .border(2.dp, colorScheme.primary, CircleShape)
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
                        color = colorScheme.primary,
                        border = androidx.compose.foundation.BorderStroke(2.dp, colors.cardSurface),
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change photo",
                                tint = colorScheme.onPrimary,
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
                        tint = colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Upload from Gallery",
                        color = colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                // Avatar Presets Tray
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Or choose avatar preset:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = colors.secondaryText,
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
                                        color = if (isSelected) colorScheme.primary else colors.divider,
                                        shape = CircleShape
                                    )
                                    .clickable { editAvatar = presetUrl }
                            )
                        }
                    }
                }

                HorizontalDivider(color = colors.divider, modifier = Modifier.padding(vertical = 4.dp))

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

                OutlinedTextField(
                    value = editUsername,
                    onValueChange = { editUsername = it },
                    label = { Text("Username") },
                    leadingIcon = {
                        Text(
                            "@",
                            fontWeight = FontWeight.Bold,
                            color = colors.secondaryText,
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

                OutlinedTextField(
                    value = editStatus,
                    onValueChange = { editStatus = it },
                    label = { Text("Status text") },
                    placeholder = { Text("e.g. 🎧 listening to music", color = colors.placeholderText) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_status_input"),
                    colors = inputColors,
                    textStyle = inputTextStyle
                )

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
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("edit_profile_save_button")
            ) {
                Text("Save", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("edit_profile_cancel_button")
            ) {
                Text("Cancel", color = colors.secondaryText)
            }
        }
    )
}

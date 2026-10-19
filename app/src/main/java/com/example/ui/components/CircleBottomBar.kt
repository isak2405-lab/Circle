package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.Screen
import com.example.ui.theme.CirclePurple
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite

@Composable
fun CircleBottomBar(
    currentScreen: Screen,
    unreadNotifications: Int,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        border = null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home / Feed
            BottomBarItem(
                selected = currentScreen is Screen.Feed,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                contentDescription = "Feed",
                testTag = "nav_feed",
                onClick = { onNavigate(Screen.Feed) }
            )

            // Explore
            BottomBarItem(
                selected = currentScreen is Screen.Explore,
                selectedIcon = Icons.Filled.Search,
                unselectedIcon = Icons.Outlined.Search,
                contentDescription = "Explore",
                testTag = "nav_explore",
                onClick = { onNavigate(Screen.Explore) }
            )

            // Create Post (Elevated Circle button)
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(CirclePurple)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple()
                    ) { onNavigate(Screen.CreatePost) }
                    .testTag("nav_create_post"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Post",
                    tint = PureWhite,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Notifications
            BottomBarItem(
                selected = currentScreen is Screen.Notifications,
                selectedIcon = Icons.Filled.Favorite,
                unselectedIcon = Icons.Outlined.FavoriteBorder,
                contentDescription = "Notifications",
                badgeCount = unreadNotifications,
                testTag = "nav_notifications",
                onClick = { onNavigate(Screen.Notifications) }
            )

            // Profile
            BottomBarItem(
                selected = currentScreen is Screen.Profile,
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                contentDescription = "Profile",
                testTag = "nav_profile",
                onClick = { onNavigate(Screen.Profile) }
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    contentDescription: String,
    badgeCount: Int = 0,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ) { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (selected) selectedIcon else unselectedIcon,
            contentDescription = contentDescription,
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(26.dp)
        )

        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(CirclePurple)
                    .align(Alignment.TopEnd)
            )
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ChatBubbleOutline
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
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.DividerOutline
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SurfaceColor

@Composable
fun CircleBottomBar(
    currentScreen: Screen,
    unreadMessages: Int,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = SurfaceColor,
        tonalElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, DividerOutline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Feed
            BottomBarNavItem(
                selected = currentScreen is Screen.Feed,
                icon = Icons.Outlined.Home,
                contentDescription = "Feed",
                testTag = "nav_feed",
                onClick = { onNavigate(Screen.Feed) }
            )

            // Explore
            BottomBarNavItem(
                selected = currentScreen is Screen.Explore,
                icon = Icons.Outlined.Search,
                contentDescription = "Explore",
                testTag = "nav_explore",
                onClick = { onNavigate(Screen.Explore) }
            )

            // Create: Compact purple rounded-square plus button (as specified in prompt)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BrandPrimary)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple()
                    ) { onNavigate(Screen.CreatePost) }
                    .testTag("nav_create_post"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create",
                    tint = PureWhite,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Messages
            BottomBarNavItem(
                selected = currentScreen is Screen.ChatList,
                icon = Icons.Outlined.ChatBubbleOutline,
                contentDescription = "Messages",
                badgeCount = unreadMessages,
                testTag = "nav_messages",
                onClick = { onNavigate(Screen.ChatList) }
            )

            // Profile
            BottomBarNavItem(
                selected = currentScreen is Screen.Profile,
                icon = Icons.Outlined.Person,
                contentDescription = "Profile",
                testTag = "nav_profile",
                onClick = { onNavigate(Screen.Profile) }
            )
        }
    }
}

@Composable
private fun BottomBarNavItem(
    selected: Boolean,
    icon: ImageVector,
    contentDescription: String,
    badgeCount: Int = 0,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ) { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (selected) BrandPrimary else SecondaryText,
            modifier = Modifier.size(24.dp)
        )

        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(BrandPrimary)
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 8.dp)
            )
        }
    }
}

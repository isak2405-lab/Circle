package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.ui.CircleViewModel
import com.example.ui.theme.CirclePurple
import com.example.ui.theme.CloseFriendsGreen

@Composable
fun NotificationsScreen(
    viewModel: CircleViewModel,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Activity",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "Close Circle Only",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("notifications_list"),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            items(notifications, key = { it.id }) { notif ->
                NotificationRow(notif = notif)
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(start = 76.dp, end = 20.dp)
                )
            }
        }
    }
}

@Composable
private fun NotificationRow(notif: NotificationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with action badge
        Box(modifier = Modifier.size(46.dp)) {
            AsyncImage(
                model = notif.user.avatarUrl,
                contentDescription = notif.user.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )

            // Micro reaction / type badge
            val badgeEmoji = when (notif.type) {
                NotificationType.REACTION_HEART -> "❤️"
                NotificationType.REACTION_LAUGH -> "😂"
                NotificationType.REACTION_FIRE -> "🔥"
                NotificationType.COMMENT -> "💬"
                NotificationType.NEW_CIRCLE_FRIEND -> "🟢"
                NotificationType.STORY_MENTION -> "✨"
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = badgeEmoji, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Notification description
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${notif.user.name} ${notif.text}",
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = notif.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Post preview thumbnail or Close Circle action
        if (notif.postPreviewUrl != null) {
            Spacer(modifier = Modifier.width(10.dp))
            AsyncImage(
                model = notif.postPreviewUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        } else if (notif.type == NotificationType.NEW_CIRCLE_FRIEND) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CloseFriendsGreen.copy(alpha = 0.15f),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = "Connected",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CloseFriendsGreen,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

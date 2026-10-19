package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val authorName: String,
    val authorUsername: String,
    val authorAvatarUrl: String,
    val type: String, // PHOTO, VIDEO, THOUGHT
    val mediaUrl: String?,
    val thoughtText: String?,
    val thoughtGradientIndex: Int,
    val filterName: String,
    val caption: String,
    val location: String?,
    val taggedFriendsRaw: String, // comma-separated
    val isCloseFriendsOnly: Boolean,
    val timestamp: String,
    val heartCount: Int,
    val laughCount: Int,
    val fireCount: Int,
    val userReaction: String?, // HEART, LAUGH, FIRE, null
    val isSaved: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val authorId: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val text: String,
    val timestamp: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val text: String?,
    val mediaUrl: String?,
    val isVoiceNote: Boolean,
    val voiceDurationSec: Int,
    val timestamp: String,
    val isRead: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val username: String,
    val avatarUrl: String,
    val bio: String,
    val statusText: String?,
    val isCloseFriend: Boolean,
    val isOnline: Boolean
)

@Entity(tableName = "security_settings")
data class SecurityEntity(
    @PrimaryKey val id: Int = 1,
    val isE2EEEnabled: Boolean = true,
    val isAppLockEnabled: Boolean = false,
    val isScreenshotProtectionEnabled: Boolean = true,
    val isAntiLeakWatermarkEnabled: Boolean = true,
    val ephemeralTimerMinutes: Int = 0,
    val safetyFingerprint: String = "SHA256:7B:4E:91:2A:F3:C8:55:0D:33:1E:8C:FA:02:64:99:A1",
    val encryptionProtocol: String = "Curve25519 + ChaCha20-Poly1305 + AES-256-GCM"
)

@Entity(tableName = "drafts")
data class DraftEntity(
    @PrimaryKey val id: Int = 1,
    val caption: String,
    val type: String,
    val mediaUrl: String?,
    val thoughtText: String,
    val filterName: String,
    val location: String,
    val isCloseFriendsOnly: Boolean
)

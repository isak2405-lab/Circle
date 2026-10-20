package com.example.data.model

enum class PostType {
    PHOTO,
    VIDEO,
    THOUGHT
}

enum class ReactionType(val emoji: String, val label: String) {
    HEART("❤️", "Love"),
    LAUGH("😂", "Haha"),
    FIRE("🔥", "Fire")
}

data class User(
    val id: String,
    val name: String,
    val username: String,
    val avatarUrl: String,
    val bio: String = "",
    val isCloseFriend: Boolean = true,
    val isOnline: Boolean = false,
    val statusText: String? = null
)

data class Comment(
    val id: String,
    val postId: String,
    val author: User,
    val text: String,
    val timestamp: String,
    val replyToUser: String? = null
)

data class Post(
    val id: String,
    val author: User,
    val type: PostType,
    val mediaUrl: String? = null,
    val thoughtText: String? = null,
    val thoughtGradientIndex: Int = 0,
    val filterName: String = "Original",
    val caption: String = "",
    val location: String? = null,
    val taggedFriends: List<String> = emptyList(),
    val isCloseFriendsOnly: Boolean = false,
    val timestamp: String = "Just now",
    val reactions: Map<ReactionType, Int> = mapOf(
        ReactionType.HEART to 0,
        ReactionType.LAUGH to 0,
        ReactionType.FIRE to 0
    ),
    val userReaction: ReactionType? = null,
    val comments: List<Comment> = emptyList(),
    val isSaved: Boolean = false
) {
    // True close friends dynamic: no public numeric like counts!
    // Shows human-centric summary instead of vanity metrics
    val reactionSummary: String
        get() {
            val total = reactions.values.sum()
            if (total == 0) return "Be the first close friend to react"
            return if (userReaction != null) {
                if (total == 1) "Reacted by you" else "Reacted by you and ${author.name.split(" ")[0]}'s circle"
            } else {
                "Reacted by ${author.name.split(" ")[0]}'s circle"
            }
        }
}

data class StorySlide(
    val id: String,
    val mediaUrl: String,
    val caption: String = "",
    val timestamp: String = "2h ago",
    val isCloseFriendsOnly: Boolean = false
)

data class UserStory(
    val user: User,
    val slides: List<StorySlide>,
    val isSeen: Boolean = false,
    val isCloseFriendsOnly: Boolean = false
)

data class ChatMessage(
    val id: String,
    val senderId: String,
    val text: String? = null,
    val mediaUrl: String? = null,
    val isVoiceNote: Boolean = false,
    val voiceDurationSec: Int = 0,
    val timestamp: String = "Now",
    val isRead: Boolean = true,
    val reaction: String? = null
)

data class Conversation(
    val id: String,
    val friend: User,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int = 0,
    val messages: List<ChatMessage> = emptyList()
)

enum class NotificationType {
    REACTION_HEART,
    REACTION_LAUGH,
    REACTION_FIRE,
    COMMENT,
    NEW_CIRCLE_FRIEND,
    STORY_MENTION
}

data class NotificationItem(
    val id: String,
    val user: User,
    val type: NotificationType,
    val text: String,
    val timestamp: String,
    val postPreviewUrl: String? = null,
    val isRead: Boolean = false
)

data class AestheticFilter(
    val id: String,
    val name: String,
    val description: String,
    val colorTone: Long,
    val contrast: Float = 1.0f,
    val saturation: Float = 1.0f
)

enum class CallType {
    VOICE,
    VIDEO
}

enum class CallState {
    CALLING,
    RINGING,
    CONNECTED,
    ENDED
}

data class ActiveCall(
    val participant: User,
    val type: CallType,
    val state: CallState = CallState.CALLING,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = true,
    val isCameraOn: Boolean = true,
    val isFrontCamera: Boolean = true,
    val encryptionFingerprint: String = "4F8B-991A-CC32-00E1"
)

enum class FeedFilterMode(val label: String, val description: String) {
    ALL_CHRONOLOGICAL("Live Chronological", "Real-time feed, zero algorithms"),
    CLOSE_FRIENDS_ONLY("Close Circle Only 🟢", "Moments shared strictly with inner circle"),
    VISUAL_MOMENTS("Visual Moments", "Photos and 60s video moments only"),
    QUIET_THOUGHTS("Quiet Thoughts", "Minimal text thoughts and reflections")
}

enum class FeedDisplayDensity(val label: String) {
    EXPANDED("Immersive (Full Cards)"),
    COMPACT("Minimal (Condensed)")
}

data class SecuritySettings(
    val isE2EEEnabled: Boolean = true,
    val isAppLockEnabled: Boolean = false,
    val isScreenshotProtectionEnabled: Boolean = true,
    val isAntiLeakWatermarkEnabled: Boolean = true,
    val ephemeralTimerMinutes: Int = 0, // 0 = off, 60 = 1h, 1440 = 24h
    val safetyFingerprint: String = "SHA256:7B:4E:91:2A:F3:C8:55:0D:33:1E:8C:FA:02:64:99:A1",
    val encryptionProtocol: String = "Curve25519 + ChaCha20-Poly1305 + AES-256-GCM",
    val isStrictInviteOnly: Boolean = true,
    val isHideReadReceipts: Boolean = false,
    val isIncognitoCalling: Boolean = true,
    val isAntiAbuseActive: Boolean = true,
    val isNudityFilterActive: Boolean = true
)

data class SupportMessage(
    val id: String,
    val isFromBot: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actionLabel: String? = null,
    val actionType: String? = null
)

data class FaqItem(
    val id: String,
    val category: String,
    val question: String,
    val answer: String
)

data class MemoryItem(
    val id: String,
    val title: String,
    val mediaUrl: String,
    val dateCreated: String,
    val unlockDate: String,
    val daysRemaining: Int,
    val isUnlocked: Boolean,
    val description: String
)



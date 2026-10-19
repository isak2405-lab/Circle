package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CircleRepository(
    private val database: OrbynDatabase? = null
) {
    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Current Authenticated User
    private val _currentUser = MutableStateFlow(
        User(
            id = "u_me",
            name = "Alex Vance",
            username = "alexvance",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
            bio = "film cameras & late night tea ☕️ living between coffee shops",
            isCloseFriend = true,
            isOnline = true,
            statusText = "🎧 listening to boygenius - Not Strong Enough"
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(true)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    // Filter selection for feed
    private val _feedFilterCloseFriendsOnly = MutableStateFlow(false)
    val feedFilterCloseFriendsOnly: StateFlow<Boolean> = _feedFilterCloseFriendsOnly.asStateFlow()

    // High Level Security Protocols State
    private val _securitySettings = MutableStateFlow(SecuritySettings())
    val securitySettings: StateFlow<SecuritySettings> = _securitySettings.asStateFlow()

    // Feed Options
    private val _feedFilterMode = MutableStateFlow(FeedFilterMode.ALL_CHRONOLOGICAL)
    val feedFilterMode: StateFlow<FeedFilterMode> = _feedFilterMode.asStateFlow()

    private val _feedDisplayDensity = MutableStateFlow(FeedDisplayDensity.EXPANDED)
    val feedDisplayDensity: StateFlow<FeedDisplayDensity> = _feedDisplayDensity.asStateFlow()

    // Call Option
    private val _activeCall = MutableStateFlow<ActiveCall?>(null)
    val activeCall: StateFlow<ActiveCall?> = _activeCall.asStateFlow()

    // Filters as specified in prompt: Original plus five filters: Golden, Film, Mono, Bloom and Dusk
    val aestheticFilters = listOf(
        AestheticFilter("f_orig", "Original", "Natural look", 0x00000000, 1.0f, 1.0f),
        AestheticFilter("f_golden", "Golden", "Warm afternoon sun", 0x25FFB300, 1.1f, 1.2f),
        AestheticFilter("f_film", "Film", "35mm grain & tones", 0x20D35400, 1.15f, 0.9f),
        AestheticFilter("f_mono", "Mono", "Restrained black & white", 0x25000000, 1.25f, 0.0f),
        AestheticFilter("f_bloom", "Bloom", "Soft highlight glow", 0x256C5CE7, 1.05f, 1.1f),
        AestheticFilter("f_dusk", "Dusk", "Deep evening quiet", 0x25262338, 1.2f, 1.05f)
    )

    // Draft Post
    private val _postDraft = MutableStateFlow<PostDraft?>(null)
    val postDraft: StateFlow<PostDraft?> = _postDraft.asStateFlow()

    data class PostDraft(
        val caption: String = "",
        val type: PostType = PostType.PHOTO,
        val mediaUrl: String? = null,
        val thoughtText: String = "",
        val filterName: String = "Original",
        val location: String = "",
        val isCloseFriendsOnly: Boolean = false
    )

    // Users in close circle
    private val friendsList = listOf(
        User("u_1", "Maya Lin", "mayalin", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&auto=format&fit=crop&q=80", "capturing small moments", isCloseFriend = true, isOnline = true, statusText = "🥐 baking sourdough"),
        User("u_2", "Leo Chen", "leochen", "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=400&auto=format&fit=crop&q=80", "brooklyn based // sound designer", isCloseFriend = true, isOnline = true, statusText = "🎛️ in the studio"),
        User("u_3", "Elena Rostova", "elenar", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80", "architecture & espresso", isCloseFriend = true, isOnline = false, statusText = "reading Calvino"),
        User("u_4", "Sam Rivera", "samriv", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80", "climbing rocks & writing poetry", isCloseFriend = false, isOnline = true, statusText = "🧗 bouldering"),
        User("u_5", "Kai Tanaka", "kaitanaka", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80", "tokyo / berlin", isCloseFriend = true, isOnline = false, statusText = "✈️ transit")
    )

    // Stories Tray
    private val _stories = MutableStateFlow<List<UserStory>>(emptyList())
    val stories: StateFlow<List<UserStory>> = _stories.asStateFlow()

    // Posts Feed
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    // Conversations / DMs
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Profile Highlights
    val profileHighlights = listOf(
        "Late Nights" to "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=300&auto=format&fit=crop&q=80",
        "Coffee & Books" to "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=300&auto=format&fit=crop&q=80",
        "35mm Film" to "https://images.unsplash.com/photo-1488161628813-04466f872be2?w=300&auto=format&fit=crop&q=80",
        "Soundtracks" to "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300&auto=format&fit=crop&q=80"
    )

    init {
        initSampleData()
        observeDatabaseIfPresent()
    }

    private fun observeDatabaseIfPresent() {
        if (database == null) return

        repositoryScope.launch {
            // Load and observe Room draft
            database.draftDao().getDraft().collect { draftEntity ->
                if (draftEntity != null) {
                    val postType = try { PostType.valueOf(draftEntity.type) } catch (e: Exception) { PostType.PHOTO }
                    _postDraft.value = PostDraft(
                        caption = draftEntity.caption,
                        type = postType,
                        mediaUrl = draftEntity.mediaUrl,
                        thoughtText = draftEntity.thoughtText,
                        filterName = draftEntity.filterName,
                        location = draftEntity.location,
                        isCloseFriendsOnly = draftEntity.isCloseFriendsOnly
                    )
                }
            }
        }

        repositoryScope.launch {
            // Load and observe Room user
            database.userDao().getCurrentUser().collect { userEntity ->
                if (userEntity != null) {
                    _currentUser.value = User(
                        id = userEntity.id,
                        name = userEntity.name,
                        username = userEntity.username,
                        avatarUrl = userEntity.avatarUrl,
                        bio = userEntity.bio,
                        isCloseFriend = userEntity.isCloseFriend,
                        isOnline = userEntity.isOnline,
                        statusText = userEntity.statusText
                    )
                }
            }
        }

        repositoryScope.launch {
            // Load and observe Room security settings
            database.securityDao().getSecuritySettings().collect { sec ->
                if (sec != null) {
                    _securitySettings.value = SecuritySettings(
                        isE2EEEnabled = sec.isE2EEEnabled,
                        isAppLockEnabled = sec.isAppLockEnabled,
                        isScreenshotProtectionEnabled = sec.isScreenshotProtectionEnabled,
                        isAntiLeakWatermarkEnabled = sec.isAntiLeakWatermarkEnabled,
                        ephemeralTimerMinutes = sec.ephemeralTimerMinutes,
                        safetyFingerprint = sec.safetyFingerprint,
                        encryptionProtocol = sec.encryptionProtocol
                    )
                }
            }
        }
    }

    private fun initSampleData() {
        // Sample Stories
        _stories.value = listOf(
            UserStory(
                user = friendsList[0], // Maya
                slides = listOf(
                    StorySlide("s_1", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop&q=80", "golden hour at fort tilden ✨", "3h ago", isCloseFriendsOnly = true),
                    StorySlide("s_2", "https://images.unsplash.com/photo-1519741497674-611481863552?w=800&auto=format&fit=crop&q=80", "dinner table chaos", "1h ago", isCloseFriendsOnly = true)
                ),
                isSeen = false,
                isCloseFriendsOnly = true
            ),
            UserStory(
                user = friendsList[1], // Leo
                slides = listOf(
                    StorySlide("s_3", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&auto=format&fit=crop&q=80", "synth jam session 🎛️", "4h ago", isCloseFriendsOnly = false)
                ),
                isSeen = false,
                isCloseFriendsOnly = false
            ),
            UserStory(
                user = friendsList[2], // Elena
                slides = listOf(
                    StorySlide("s_4", "https://images.unsplash.com/photo-1493857671505-72967e2e2760?w=800&auto=format&fit=crop&q=80", "morning walk in the rain", "6h ago", isCloseFriendsOnly = true)
                ),
                isSeen = false,
                isCloseFriendsOnly = true
            ),
            UserStory(
                user = friendsList[3], // Sam
                slides = listOf(
                    StorySlide("s_5", "https://images.unsplash.com/photo-1522163182402-834f871fd851?w=800&auto=format&fit=crop&q=80", "high above the clouds", "8h ago", isCloseFriendsOnly = false)
                ),
                isSeen = true,
                isCloseFriendsOnly = false
            ),
            UserStory(
                user = friendsList[4], // Kai
                slides = listOf(
                    StorySlide("s_6", "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=800&auto=format&fit=crop&q=80", "rainy shinjuku alleys", "12h ago", isCloseFriendsOnly = true)
                ),
                isSeen = true,
                isCloseFriendsOnly = true
            )
        )

        // Sample Posts (Photo, Video, Thought)
        _posts.value = listOf(
            Post(
                id = "p_1",
                author = friendsList[0], // Maya
                type = PostType.PHOTO,
                mediaUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=900&auto=format&fit=crop&q=80",
                filterName = "Golden",
                caption = "sunday morning stillness. coffee tastes better when no one is in a rush.",
                location = "Café Kitsuné, West Village",
                taggedFriends = listOf("alexvance", "leochen"),
                isCloseFriendsOnly = true,
                timestamp = "24m ago",
                reactions = mapOf(
                    ReactionType.HEART to 4,
                    ReactionType.LAUGH to 0,
                    ReactionType.FIRE to 2
                ),
                userReaction = ReactionType.HEART,
                comments = listOf(
                    Comment("c_1", "p_1", friendsList[1], "that sunlight angle is pure cinema", "18m ago"),
                    Comment("c_2", "p_1", _currentUser.value, "save a croissant for me next time!", "12m ago")
                )
            ),
            Post(
                id = "p_2",
                author = friendsList[1], // Leo
                type = PostType.VIDEO,
                mediaUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=900&auto=format&fit=crop&q=80",
                filterName = "Bloom",
                caption = "sampled the rain hitting my fire escape and turned it into an ambient loop. 48 seconds of quiet.",
                location = "Studio B, Greenpoint",
                isCloseFriendsOnly = false,
                timestamp = "2h ago",
                reactions = mapOf(
                    ReactionType.HEART to 3,
                    ReactionType.LAUGH to 1,
                    ReactionType.FIRE to 6
                ),
                userReaction = ReactionType.FIRE,
                comments = listOf(
                    Comment("c_3", "p_2", friendsList[2], "putting this on my sleep playlist immediately", "1h ago")
                )
            ),
            Post(
                id = "p_3",
                author = friendsList[2], // Elena
                type = PostType.THOUGHT,
                thoughtText = "the most comforting feeling is sitting in silence with people you don't feel pressured to entertain. that's what orbyn is for.",
                thoughtGradientIndex = 2,
                caption = "",
                location = "Home Library",
                isCloseFriendsOnly = true,
                timestamp = "4h ago",
                reactions = mapOf(
                    ReactionType.HEART to 5,
                    ReactionType.LAUGH to 0,
                    ReactionType.FIRE to 1
                ),
                userReaction = null,
                isSaved = true,
                comments = listOf(
                    Comment("c_4", "p_3", friendsList[0], "felt this deeply today ❤️", "3h ago")
                )
            ),
            Post(
                id = "p_4",
                author = friendsList[4], // Kai
                type = PostType.PHOTO,
                mediaUrl = "https://images.unsplash.com/photo-1542051841857-5f90071e7989?w=900&auto=format&fit=crop&q=80",
                filterName = "Film",
                caption = "midnight convenience store run in shibuya. the ramen hit different at 2am.",
                location = "Shibuya, Tokyo",
                isCloseFriendsOnly = true,
                timestamp = "7h ago",
                reactions = mapOf(
                    ReactionType.HEART to 3,
                    ReactionType.LAUGH to 2,
                    ReactionType.FIRE to 4
                ),
                userReaction = null,
                comments = emptyList()
            )
        )

        // Sample Conversations
        _conversations.value = listOf(
            Conversation(
                id = "conv_1",
                friend = friendsList[0], // Maya
                lastMessage = "are we still meeting for matcha at 4?",
                timestamp = "12m ago",
                unreadCount = 1,
                messages = listOf(
                    ChatMessage("m_1", friendsList[0].id, "hey! look at this film scan I just developed", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&auto=format&fit=crop&q=80", false, 0, "10:14 AM", true),
                    ChatMessage("m_2", "u_me", "whoa the colors came out incredible! portra 400?", null, false, 0, "10:18 AM", true),
                    ChatMessage("m_3", friendsList[0].id, null, null, true, 14, "10:22 AM", true),
                    ChatMessage("m_4", "u_me", "haha totally agree", null, false, 0, "10:25 AM", true),
                    ChatMessage("m_5", friendsList[0].id, "are we still meeting for matcha at 4?", null, false, 0, "10:45 AM", false)
                )
            ),
            Conversation(
                id = "conv_2",
                friend = friendsList[1], // Leo
                lastMessage = "Sent a voice note (0:32)",
                timestamp = "1h ago",
                unreadCount = 0,
                messages = listOf(
                    ChatMessage("m_6", friendsList[1].id, "check out the draft track when you get home", null, false, 0, "9:00 AM", true),
                    ChatMessage("m_7", "u_me", "listening right now! the bassline is crazy clean", null, false, 0, "9:15 AM", true),
                    ChatMessage("m_8", friendsList[1].id, null, null, true, 32, "9:20 AM", true)
                )
            ),
            Conversation(
                id = "conv_3",
                friend = friendsList[2], // Elena
                lastMessage = "Elena reacted ❤️ to your photo",
                timestamp = "3h ago",
                unreadCount = 0,
                messages = listOf(
                    ChatMessage("m_9", friendsList[2].id, "loved your thought on the feed today", null, false, 0, "Yesterday", true),
                    ChatMessage("m_10", "u_me", "thank you Elena! means a lot", null, false, 0, "Yesterday", true)
                )
            )
        )

        // Sample Notifications
        _notifications.value = listOf(
            NotificationItem(
                id = "notif_1",
                user = friendsList[0],
                type = NotificationType.REACTION_FIRE,
                text = "reacted 🔥 to your rooftop film shot",
                timestamp = "10m ago",
                postPreviewUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=200&auto=format&fit=crop&q=80"
            ),
            NotificationItem(
                id = "notif_2",
                user = friendsList[1],
                type = NotificationType.COMMENT,
                text = "commented: \"that sunlight angle is pure cinema\"",
                timestamp = "18m ago",
                postPreviewUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=200&auto=format&fit=crop&q=80"
            ),
            NotificationItem(
                id = "notif_3",
                user = friendsList[2],
                type = NotificationType.NEW_CIRCLE_FRIEND,
                text = "added you to their Close Circle 🟢",
                timestamp = "2h ago"
            ),
            NotificationItem(
                id = "notif_4",
                user = friendsList[0],
                type = NotificationType.STORY_MENTION,
                text = "mentioned you in their 24h story",
                timestamp = "3h ago",
                postPreviewUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=200&auto=format&fit=crop&q=80"
            )
        )
    }

    fun toggleFeedFilterCloseFriends() {
        _feedFilterCloseFriendsOnly.update { !it }
    }

    // Toggle Reaction: ❤️, 😂, 🔥 (mutually exclusive)
    fun reactToPost(postId: String, reaction: ReactionType) {
        _posts.update { currentPosts ->
            currentPosts.map { post ->
                if (post.id == postId) {
                    val currentReactions = post.reactions.toMutableMap()
                    val existingUserReaction = post.userReaction

                    if (existingUserReaction == reaction) {
                        currentReactions[reaction] = (currentReactions[reaction] ?: 1) - 1
                        post.copy(reactions = currentReactions, userReaction = null)
                    } else {
                        if (existingUserReaction != null) {
                            currentReactions[existingUserReaction] = (currentReactions[existingUserReaction] ?: 1) - 1
                        }
                        currentReactions[reaction] = (currentReactions[reaction] ?: 0) + 1
                        post.copy(reactions = currentReactions, userReaction = reaction)
                    }
                } else {
                    post
                }
            }
        }
    }

    fun addComment(postId: String, text: String) {
        if (text.isBlank()) return
        val newComment = Comment(
            id = "c_${System.currentTimeMillis()}",
            postId = postId,
            author = _currentUser.value,
            text = text.trim(),
            timestamp = "Just now"
        )
        _posts.update { currentPosts ->
            currentPosts.map { post ->
                if (post.id == postId) {
                    post.copy(comments = post.comments + newComment)
                } else {
                    post
                }
            }
        }

        // Persist to internal Room SQLite database
        database?.let { db ->
            repositoryScope.launch {
                db.commentDao().insertComment(
                    CommentEntity(
                        id = newComment.id,
                        postId = newComment.postId,
                        authorId = newComment.author.id,
                        authorName = newComment.author.name,
                        authorAvatarUrl = newComment.author.avatarUrl,
                        text = newComment.text,
                        timestamp = newComment.timestamp
                    )
                )
            }
        }
    }

    fun toggleSavePost(postId: String) {
        _posts.update { currentPosts ->
            currentPosts.map { post ->
                if (post.id == postId) post.copy(isSaved = !post.isSaved) else post
            }
        }
    }

    fun createPost(
        type: PostType,
        mediaUrl: String?,
        thoughtText: String?,
        filterName: String,
        caption: String,
        location: String?,
        taggedFriends: List<String>,
        isCloseFriendsOnly: Boolean
    ) {
        val newPost = Post(
            id = "p_${System.currentTimeMillis()}",
            author = _currentUser.value,
            type = type,
            mediaUrl = mediaUrl,
            thoughtText = thoughtText,
            thoughtGradientIndex = (0..3).random(),
            filterName = filterName,
            caption = caption,
            location = location?.ifBlank { null },
            taggedFriends = taggedFriends,
            isCloseFriendsOnly = isCloseFriendsOnly,
            timestamp = "Just now",
            reactions = mapOf(
                ReactionType.HEART to 0,
                ReactionType.LAUGH to 0,
                ReactionType.FIRE to 0
            ),
            userReaction = null,
            comments = emptyList()
        )
        _posts.update { listOf(newPost) + it }
        _postDraft.value = null

        // Persist internally to Room SQLite database
        database?.let { db ->
            repositoryScope.launch {
                db.postDao().insertPost(
                    PostEntity(
                        id = newPost.id,
                        authorId = newPost.author.id,
                        authorName = newPost.author.name,
                        authorUsername = newPost.author.username,
                        authorAvatarUrl = newPost.author.avatarUrl,
                        type = newPost.type.name,
                        mediaUrl = newPost.mediaUrl,
                        thoughtText = newPost.thoughtText,
                        thoughtGradientIndex = newPost.thoughtGradientIndex,
                        filterName = newPost.filterName,
                        caption = newPost.caption,
                        location = newPost.location,
                        taggedFriendsRaw = newPost.taggedFriends.joinToString(","),
                        isCloseFriendsOnly = newPost.isCloseFriendsOnly,
                        timestamp = newPost.timestamp,
                        heartCount = 0,
                        laughCount = 0,
                        fireCount = 0,
                        userReaction = null,
                        isSaved = false
                    )
                )
                db.draftDao().clearDraft()
            }
        }
    }

    fun saveDraft(draft: PostDraft) {
        _postDraft.value = draft
        // Persist draft to internal Room SQLite database
        database?.let { db ->
            repositoryScope.launch {
                db.draftDao().insertDraft(
                    DraftEntity(
                        id = 1,
                        caption = draft.caption,
                        type = draft.type.name,
                        mediaUrl = draft.mediaUrl,
                        thoughtText = draft.thoughtText,
                        filterName = draft.filterName,
                        location = draft.location,
                        isCloseFriendsOnly = draft.isCloseFriendsOnly
                    )
                )
            }
        }
    }

    fun clearDraft() {
        _postDraft.value = null
        database?.let { db ->
            repositoryScope.launch {
                db.draftDao().clearDraft()
            }
        }
    }

    fun markStorySeen(userId: String) {
        _stories.update { list ->
            list.map { if (it.user.id == userId) it.copy(isSeen = true) else it }
        }
    }

    fun addStory(mediaUrl: String, caption: String, isCloseFriendsOnly: Boolean) {
        val newSlide = StorySlide(
            id = "s_${System.currentTimeMillis()}",
            mediaUrl = mediaUrl,
            caption = caption,
            timestamp = "Just now",
            isCloseFriendsOnly = isCloseFriendsOnly
        )
        val myStory = _stories.value.find { it.user.id == _currentUser.value.id }
        if (myStory != null) {
            _stories.update { list ->
                list.map {
                    if (it.user.id == _currentUser.value.id) {
                        it.copy(slides = it.slides + newSlide, isSeen = false)
                    } else it
                }
            }
        } else {
            val userStory = UserStory(
                user = _currentUser.value,
                slides = listOf(newSlide),
                isSeen = false,
                isCloseFriendsOnly = isCloseFriendsOnly
            )
            _stories.update { listOf(userStory) + it }
        }
    }

    fun sendMessage(conversationId: String, text: String?, isVoiceNote: Boolean = false, voiceDuration: Int = 0) {
        if (text.isNullOrBlank() && !isVoiceNote) return
        val newMsg = ChatMessage(
            id = "m_${System.currentTimeMillis()}",
            senderId = "u_me",
            text = text,
            isVoiceNote = isVoiceNote,
            voiceDurationSec = voiceDuration,
            timestamp = "Just now",
            isRead = true
        )
        _conversations.update { convList ->
            convList.map { conv ->
                if (conv.id == conversationId) {
                    conv.copy(
                        lastMessage = if (isVoiceNote) "Sent a voice note (${voiceDuration}s)" else (text ?: ""),
                        timestamp = "Just now",
                        messages = conv.messages + newMsg
                    )
                } else conv
            }
        }

        // Persist message to Room
        database?.let { db ->
            repositoryScope.launch {
                db.messageDao().insertMessage(
                    MessageEntity(
                        id = newMsg.id,
                        conversationId = conversationId,
                        senderId = newMsg.senderId,
                        text = newMsg.text,
                        mediaUrl = newMsg.mediaUrl,
                        isVoiceNote = newMsg.isVoiceNote,
                        voiceDurationSec = newMsg.voiceDurationSec,
                        timestamp = newMsg.timestamp,
                        isRead = newMsg.isRead
                    )
                )
            }
        }
    }

    fun simulateFriendReply(conversationId: String) {
        val conv = _conversations.value.find { it.id == conversationId } ?: return
        val replyTemplates = listOf(
            "love that! let's do it",
            "yes!! exactly what I was thinking",
            "just listened, sounds incredible 🔥",
            "heading out now, see you in 10!",
            "omg haha you're so right 😂"
        )
        val reply = ChatMessage(
            id = "m_${System.currentTimeMillis()}",
            senderId = conv.friend.id,
            text = replyTemplates.random(),
            timestamp = "Just now",
            isRead = false
        )
        _conversations.update { convList ->
            convList.map { c ->
                if (c.id == conversationId) {
                    c.copy(
                        lastMessage = reply.text ?: "",
                        timestamp = "Just now",
                        messages = c.messages + reply
                    )
                } else c
            }
        }

        database?.let { db ->
            repositoryScope.launch {
                db.messageDao().insertMessage(
                    MessageEntity(
                        id = reply.id,
                        conversationId = conversationId,
                        senderId = reply.senderId,
                        text = reply.text,
                        mediaUrl = reply.mediaUrl,
                        isVoiceNote = reply.isVoiceNote,
                        voiceDurationSec = reply.voiceDurationSec,
                        timestamp = reply.timestamp,
                        isRead = reply.isRead
                    )
                )
            }
        }
    }

    fun updateProfile(name: String, username: String, bio: String, avatarUrl: String, statusText: String) {
        val updated = User(
            id = "u_me",
            name = name.trim(),
            username = username.trim().removePrefix("@"),
            bio = bio.trim(),
            avatarUrl = avatarUrl,
            statusText = statusText.trim().ifBlank { null },
            isCloseFriend = true,
            isOnline = true
        )
        _currentUser.value = updated

        database?.let { db ->
            repositoryScope.launch {
                db.userDao().insertUser(
                    UserEntity(
                        id = updated.id,
                        name = updated.name,
                        username = updated.username,
                        avatarUrl = updated.avatarUrl,
                        bio = updated.bio,
                        statusText = updated.statusText,
                        isCloseFriend = updated.isCloseFriend,
                        isOnline = updated.isOnline
                    )
                )
            }
        }
    }

    fun login(email: String, username: String) {
        _currentUser.update {
            it.copy(
                name = username.replaceFirstChar { char -> char.uppercase() },
                username = username.lowercase().replace(" ", "")
            )
        }
        _isAuthenticated.value = true
    }

    fun logout() {
        _isAuthenticated.value = false
    }

    fun getFriends(): List<User> = friendsList

    // Security Controls
    fun updateSecuritySettings(newSettings: SecuritySettings) {
        _securitySettings.value = newSettings
        database?.let { db ->
            repositoryScope.launch {
                db.securityDao().insertSecuritySettings(
                    SecurityEntity(
                        id = 1,
                        isE2EEEnabled = newSettings.isE2EEEnabled,
                        isAppLockEnabled = newSettings.isAppLockEnabled,
                        isScreenshotProtectionEnabled = newSettings.isScreenshotProtectionEnabled,
                        isAntiLeakWatermarkEnabled = newSettings.isAntiLeakWatermarkEnabled,
                        ephemeralTimerMinutes = newSettings.ephemeralTimerMinutes,
                        safetyFingerprint = newSettings.safetyFingerprint,
                        encryptionProtocol = newSettings.encryptionProtocol
                    )
                )
            }
        }
    }

    // Feed Options Controls
    fun setFeedFilterMode(mode: FeedFilterMode) {
        _feedFilterMode.value = mode
        _feedFilterCloseFriendsOnly.value = (mode == FeedFilterMode.CLOSE_FRIENDS_ONLY)
    }

    fun setFeedDisplayDensity(density: FeedDisplayDensity) {
        _feedDisplayDensity.value = density
    }

    // Call Engine Controls
    fun startCall(participant: User, type: CallType) {
        _activeCall.value = ActiveCall(
            participant = participant,
            type = type,
            state = CallState.CALLING,
            durationSeconds = 0,
            isMuted = false,
            isSpeakerOn = (type == CallType.VIDEO),
            isCameraOn = (type == CallType.VIDEO)
        )
    }

    fun setCallConnected() {
        _activeCall.update { it?.copy(state = CallState.CONNECTED) }
    }

    fun tickCallDuration() {
        _activeCall.update {
            if (it?.state == CallState.CONNECTED) {
                it.copy(durationSeconds = it.durationSeconds + 1)
            } else it
        }
    }

    fun toggleMuteCall() {
        _activeCall.update { it?.copy(isMuted = !it.isMuted) }
    }

    fun toggleSpeakerCall() {
        _activeCall.update { it?.copy(isSpeakerOn = !it.isSpeakerOn) }
    }

    fun toggleCameraCall() {
        _activeCall.update { it?.copy(isCameraOn = !it.isCameraOn) }
    }

    fun flipCameraCall() {
        _activeCall.update { it?.copy(isFrontCamera = !it.isFrontCamera) }
    }

    fun endCall() {
        _activeCall.update { it?.copy(state = CallState.ENDED) }
        _activeCall.value = null
    }
}

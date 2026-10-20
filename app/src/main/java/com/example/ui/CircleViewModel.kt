package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.CircleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    data object Feed : Screen()
    data object Explore : Screen()
    data object CreatePost : Screen()
    data object Notifications : Screen()
    data object Profile : Screen()
    data class StoryViewer(val initialUserStoryIndex: Int) : Screen()
    data object ChatList : Screen()
    data class ChatDetail(val conversationId: String) : Screen()
    data object Call : Screen()
    data object SecurityVault : Screen()
    data object SupportBot : Screen()
    data object Splash : Screen()
    data object Auth : Screen()
}

class CircleViewModel(
    val repository: CircleRepository = CircleRepository()
) : ViewModel() {

    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Splash))
    val currentScreen: StateFlow<Screen> = _screenStack.map { it.lastOrNull() ?: Screen.Feed }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Screen.Splash)

    val currentUser = repository.currentUser
    val isAuthenticated = repository.isAuthenticated
    val feedFilterCloseFriendsOnly = repository.feedFilterCloseFriendsOnly
    val securitySettings = repository.securitySettings
    val feedFilterMode = repository.feedFilterMode
    val feedDisplayDensity = repository.feedDisplayDensity
    val activeCall = repository.activeCall
    val posts = repository.posts
    val stories = repository.stories
    val conversations = repository.conversations
    val notifications = repository.notifications
    val aestheticFilters = repository.aestheticFilters
    val postDraft = repository.postDraft

    // Active Comments Sheet State
    private val _activeCommentPostId = MutableStateFlow<String?>(null)
    val activeCommentPostId: StateFlow<String?> = _activeCommentPostId.asStateFlow()

    // Explore Search
    private val _exploreSearchQuery = MutableStateFlow("")
    val exploreSearchQuery: StateFlow<String> = _exploreSearchQuery.asStateFlow()

    private val _selectedExploreTag = MutableStateFlow<String?>(null)
    val selectedExploreTag: StateFlow<String?> = _selectedExploreTag.asStateFlow()

    // Active Voice Note Playing ID
    private val _playingVoiceNoteId = MutableStateFlow<String?>(null)
    val playingVoiceNoteId: StateFlow<String?> = _playingVoiceNoteId.asStateFlow()

    // Flying emoji reaction animation state
    private val _reactionBurst = MutableStateFlow<Pair<String, ReactionType>?>(null)
    val reactionBurst: StateFlow<Pair<String, ReactionType>?> = _reactionBurst.asStateFlow()

    init {
        // Observe auth state
        viewModelScope.launch {
            isAuthenticated.collect { authed ->
                if (!authed) {
                    _screenStack.value = listOf(Screen.Auth)
                } else if (_screenStack.value.firstOrNull() == Screen.Auth) {
                    _screenStack.value = listOf(Screen.Feed)
                }
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _screenStack.update { stack ->
            if (screen is Screen.Feed || screen is Screen.Explore || screen is Screen.Notifications || screen is Screen.Profile) {
                // Top-level tab navigation
                listOf(screen)
            } else {
                stack + screen
            }
        }
    }

    fun navigateBack(): Boolean {
        if (_screenStack.value.size > 1) {
            _screenStack.update { it.dropLast(1) }
            return true
        }
        return false
    }

    fun toggleCloseFriendsFilter() {
        repository.toggleFeedFilterCloseFriends()
    }

    fun reactToPost(postId: String, reaction: ReactionType) {
        repository.reactToPost(postId, reaction)
        // Trigger burst animation
        _reactionBurst.value = Pair(postId, reaction)
        viewModelScope.launch {
            delay(1200)
            if (_reactionBurst.value?.first == postId) {
                _reactionBurst.value = null
            }
        }
    }

    fun openComments(postId: String) {
        _activeCommentPostId.value = postId
    }

    fun closeComments() {
        _activeCommentPostId.value = null
    }

    fun addComment(postId: String, text: String) {
        repository.addComment(postId, text)
    }

    fun toggleSavePost(postId: String) {
        repository.toggleSavePost(postId)
    }

    fun openStoryViewer(index: Int) {
        navigateTo(Screen.StoryViewer(index))
    }

    fun markStorySeen(userId: String) {
        repository.markStorySeen(userId)
    }

    fun openChatDetail(conversationId: String) {
        navigateTo(Screen.ChatDetail(conversationId))
    }

    fun sendMessage(conversationId: String, text: String?, isVoiceNote: Boolean = false, voiceDuration: Int = 0) {
        repository.sendMessage(conversationId, text, isVoiceNote, voiceDuration)
        // Simulate immediate response after 2 seconds for a lively chat feel
        viewModelScope.launch {
            delay(1800)
            repository.simulateFriendReply(conversationId)
        }
    }

    fun togglePlayVoiceNote(messageId: String) {
        if (_playingVoiceNoteId.value == messageId) {
            _playingVoiceNoteId.value = null
        } else {
            _playingVoiceNoteId.value = messageId
            viewModelScope.launch {
                delay(3000)
                if (_playingVoiceNoteId.value == messageId) {
                    _playingVoiceNoteId.value = null
                }
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
        repository.createPost(
            type,
            mediaUrl,
            thoughtText,
            filterName,
            caption,
            location,
            taggedFriends,
            isCloseFriendsOnly
        )
        navigateBack()
    }

    fun saveDraft(
        caption: String,
        type: PostType,
        mediaUrl: String?,
        thoughtText: String,
        filterName: String,
        location: String,
        isCloseFriendsOnly: Boolean
    ) {
        repository.saveDraft(
            CircleRepository.PostDraft(
                caption,
                type,
                mediaUrl,
                thoughtText,
                filterName,
                location,
                isCloseFriendsOnly
            )
        )
    }

    fun clearDraft() {
        repository.clearDraft()
    }

    fun updateSearchQuery(query: String) {
        _exploreSearchQuery.value = query
    }

    fun selectExploreTag(tag: String?) {
        _selectedExploreTag.value = tag
    }

    fun updateProfile(name: String, username: String, bio: String, avatarUrl: String, statusText: String) {
        repository.updateProfile(name, username, bio, avatarUrl, statusText)
    }

    fun login(email: String, username: String) {
        repository.login(email, username)
        _screenStack.value = listOf(Screen.Feed)
    }

    fun logout() {
        repository.logout()
    }

    fun getFriends(): List<User> = repository.getFriends()

    // Call Engine Controls
    private var callTimerJob: kotlinx.coroutines.Job? = null

    fun startCall(participant: User, type: CallType) {
        repository.startCall(participant, type)
        navigateTo(Screen.Call)
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            delay(1500)
            repository.setCallConnected()
            while (true) {
                delay(1000)
                repository.tickCallDuration()
            }
        }
    }

    fun endCall() {
        callTimerJob?.cancel()
        repository.endCall()
        navigateBack()
    }

    fun toggleMuteCall() = repository.toggleMuteCall()
    fun toggleSpeakerCall() = repository.toggleSpeakerCall()
    fun toggleCameraCall() = repository.toggleCameraCall()
    fun flipCameraCall() = repository.flipCameraCall()

    // Feed Options Controls
    fun setFeedFilterMode(mode: FeedFilterMode) = repository.setFeedFilterMode(mode)
    fun setFeedDisplayDensity(density: FeedDisplayDensity) = repository.setFeedDisplayDensity(density)

    // Security Settings Controls
    fun updateSecuritySettings(settings: SecuritySettings) = repository.updateSecuritySettings(settings)

    fun regenerateSecurityKeys() {
        val chars = "0123456789ABCDEF"
        val chunks = (1..8).map {
            (1..2).map { chars.random() }.joinToString("")
        }.joinToString(":")
        val newFingerprint = "SHA256:$chunks"
        val current = securitySettings.value
        updateSecuritySettings(current.copy(safetyFingerprint = newFingerprint))
    }

    // Support Bot & FAQ Assistant State
    private val _supportMessages = MutableStateFlow<List<com.example.data.model.SupportMessage>>(
        listOf(
            com.example.data.model.SupportMessage(
                id = "msg_welcome",
                isFromBot = true,
                text = "Hi there! 👋 I'm Orbit, your Circle Support & Security Assistant.\n\nAsk me anything about fixing Google Play download issues, setting up invite-only privacy, configuring biometric lock, or editing your profile."
            )
        )
    )
    val supportMessages: StateFlow<List<com.example.data.model.SupportMessage>> = _supportMessages.asStateFlow()

    fun sendSupportQuery(rawQuery: String) {
        val query = rawQuery.trim()
        if (query.isBlank()) return

        val userMsg = com.example.data.model.SupportMessage(
            id = "u_${System.currentTimeMillis()}",
            isFromBot = false,
            text = query
        )
        _supportMessages.value = _supportMessages.value + userMsg

        viewModelScope.launch {
            kotlinx.coroutines.delay(350)
            val normalized = query.lowercase(java.util.Locale.ROOT)
            val botResponse: com.example.data.model.SupportMessage = when {
                normalized.contains("download") || normalized.contains("play store") || normalized.contains("age") || normalized.contains("restriction") || normalized.contains("wife") -> {
                    com.example.data.model.SupportMessage(
                        id = "b_${System.currentTimeMillis()}",
                        isFromBot = true,
                        text = "📱 **Fixing Google Play Age Restriction & Downloads:**\n\n1. **Retake Content Questionnaire:** In Google Play Console under *Policy > App Content > Content Ratings*, answer 'No' to explicit content/violence. This drops the rating from 18+ to Teen/PEGI 12.\n2. **Target Audience:** Select 13–15, 16–17, and 18+.\n3. **If in Internal Testing:** Make sure your wife's email is in the *Internal testing > Testers* list, and she clicks the web opt-in invite link.\n4. **Device Check:** Check *Play Store > Settings > Family > Parental Controls*.",
                        actionLabel = "Open Security Vault",
                        actionType = "SECURITY_VAULT"
                    )
                }
                normalized.contains("invite") || normalized.contains("friend") || normalized.contains("stranger") || normalized.contains("private") -> {
                    com.example.data.model.SupportMessage(
                        id = "b_${System.currentTimeMillis()}",
                        isFromBot = true,
                        text = "🔒 **Strict Invite-Only Circle:**\n\nYes! When 'Strict Invite-Only' is turned ON in your Security Vault:\n• Only mutual, accepted friends can see your moments, stories, or send direct messages.\n• Your profile will never appear in global public search or directories.",
                        actionLabel = "Check Security Settings",
                        actionType = "SECURITY_VAULT"
                    )
                }
                normalized.contains("security") || normalized.contains("encrypt") || normalized.contains("e2ee") || normalized.contains("key") || normalized.contains("vault") -> {
                    com.example.data.model.SupportMessage(
                        id = "b_${System.currentTimeMillis()}",
                        isFromBot = true,
                        text = "🛡️ **Security Architecture:**\n\n• **End-to-End Encryption:** Curve25519 key exchange + ChaCha20-Poly1305 and AES-256-GCM.\n• **Screenshot Shield:** Blocks OS screenshot capture on private moments.\n• **Anti-Leak Watermarks:** Invisible cryptographic signature embeds in shared media.\n• **Key Rotation:** You can tap 'Rotate Keys' in the Security Vault anytime.",
                        actionLabel = "Open Security Vault",
                        actionType = "SECURITY_VAULT"
                    )
                }
                normalized.contains("nude") || normalized.contains("nudity") || normalized.contains("abuse") || normalized.contains("harass") || normalized.contains("safety") -> {
                    com.example.data.model.SupportMessage(
                        id = "b_${System.currentTimeMillis()}",
                        isFromBot = true,
                        text = "🛑 **On-Device Safety & Anti-Abuse Guard:**\n\n• Real-time client-side keyword and skin-chroma analysis screens posts, captions, and comments *before* they are shared.\n• Zero cloud surveillance: your media is never sent unencrypted to external servers.\n• You can also report and instantly hide any post directly from the moment card.",
                        actionLabel = "View Safety Guard",
                        actionType = "SECURITY_VAULT"
                    )
                }
                normalized.contains("photo") || normalized.contains("avatar") || normalized.contains("picture") || normalized.contains("edit") -> {
                    com.example.data.model.SupportMessage(
                        id = "b_${System.currentTimeMillis()}",
                        isFromBot = true,
                        text = "📸 **Updating Your Profile Picture:**\n\n1. Go to your Profile tab.\n2. Tap directly on your avatar (or tap 'Account & Profile' in Settings).\n3. Tap 'Upload from Gallery' to pick any photo from your phone, or pick one of the aesthetic presets!\n4. Tap 'Save' to apply instantly.",
                        actionLabel = "Go to Profile",
                        actionType = "PROFILE"
                    )
                }
                normalized.contains("call") || normalized.contains("video") || normalized.contains("audio") || normalized.contains("voice") -> {
                    com.example.data.model.SupportMessage(
                        id = "b_${System.currentTimeMillis()}",
                        isFromBot = true,
                        text = "📞 **Encrypted Calling:**\n\n• Start a 1-on-1 Voice or Video call inside any chat.\n• Calls feature peer-to-peer encryption with a verified safety fingerprint.\n• You can enable 'Relay Calling' in the Vault to protect your IP address.",
                        actionLabel = "Open Security Vault",
                        actionType = "SECURITY_VAULT"
                    )
                }
                else -> {
                    com.example.data.model.SupportMessage(
                        id = "b_${System.currentTimeMillis()}",
                        isFromBot = true,
                        text = "I received your question: \"$query\". Circle is built with zero advertising and strict close-friends privacy. Feel free to tap any of the quick topics above or check our Security Vault for full control.",
                        actionLabel = "Open Security Vault",
                        actionType = "SECURITY_VAULT"
                    )
                }
            }
            _supportMessages.value = _supportMessages.value + botResponse
        }
    }

    companion object {
        fun provideFactory(context: android.content.Context): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    val db = com.example.data.local.OrbynDatabase.getInstance(context)
                    val repo = com.example.data.repository.CircleRepository(db)
                    return CircleViewModel(repo) as T
                }
            }
    }
}

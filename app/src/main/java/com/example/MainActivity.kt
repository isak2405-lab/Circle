package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.CircleViewModel
import com.example.ui.Screen
import com.example.ui.components.CircleBottomBar
import com.example.ui.screens.*
import com.example.ui.theme.CircleTheme

import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CircleTheme {
                CircleApp()
            }
        }
    }
}

@Composable
fun CircleApp(viewModel: CircleViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadNotifications = notifications.count { !it.isRead }

    val isPrimaryTab = currentScreen is Screen.Feed ||
            currentScreen is Screen.Explore ||
            currentScreen is Screen.Notifications ||
            currentScreen is Screen.Profile

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isPrimaryTab) {
                CircleBottomBar(
                    currentScreen = currentScreen,
                    unreadNotifications = unreadNotifications,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (isPrimaryTab) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            when (val screen = currentScreen) {
                is Screen.Feed -> {
                    FeedScreen(viewModel = viewModel)
                }
                is Screen.Explore -> {
                    ExploreScreen(
                        viewModel = viewModel,
                        onPostClick = { /* open post */ }
                    )
                }
                is Screen.CreatePost -> {
                    CreatePostScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is Screen.Notifications -> {
                    NotificationsScreen(viewModel = viewModel)
                }
                is Screen.Profile -> {
                    ProfileScreen(viewModel = viewModel)
                }
                is Screen.StoryViewer -> {
                    StoryViewerScreen(
                        initialIndex = screen.initialUserStoryIndex,
                        viewModel = viewModel,
                        onClose = { viewModel.navigateBack() }
                    )
                }
                is Screen.ChatList -> {
                    ChatListScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() },
                        onOpenChat = { convId -> viewModel.openChatDetail(convId) }
                    )
                }
                is Screen.ChatDetail -> {
                    ChatDetailScreen(
                        conversationId = screen.conversationId,
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is Screen.Call -> {
                    CallScreen(
                        viewModel = viewModel,
                        onEndCall = { viewModel.endCall() }
                    )
                }
                is Screen.SecurityVault -> {
                    SecurityVaultScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is Screen.Auth -> {
                    AuthScreen(viewModel = viewModel)
                }
            }
        }
    }
}

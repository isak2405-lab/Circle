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
fun CircleApp(
    viewModel: CircleViewModel = viewModel(
        factory = CircleViewModel.provideFactory(androidx.compose.ui.platform.LocalContext.current)
    )
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val unreadMessages = conversations.sumOf { it.unreadCount }

    val isPrimaryTab = currentScreen is Screen.Feed ||
            currentScreen is Screen.Explore ||
            currentScreen is Screen.Memories ||
            currentScreen is Screen.ChatList ||
            currentScreen is Screen.Profile

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isPrimaryTab) {
                CircleBottomBar(
                    currentScreen = currentScreen,
                    unreadMessages = unreadMessages,
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
                is Screen.Memories -> {
                    MemoriesScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
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
                is Screen.SecurityVault -> {
                    SecurityVaultScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is Screen.SupportBot -> {
                    SupportFaqScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is Screen.Splash -> {
                    WavySplashScreen(
                        onFinish = { viewModel.navigateTo(Screen.Feed) }
                    )
                }
                is Screen.Auth -> {
                    AuthScreen(viewModel = viewModel)
                }
            }
        }
    }
}

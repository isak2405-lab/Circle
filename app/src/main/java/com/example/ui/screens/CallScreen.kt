package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ActiveCall
import com.example.data.model.CallState
import com.example.data.model.CallType
import com.example.ui.CircleViewModel
import com.example.ui.theme.CirclePurple
import com.example.ui.theme.CloseFriendsGreen
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite

@Composable
fun CallScreen(
    viewModel: CircleViewModel,
    onEndCall: () -> Unit
) {
    BackHandler { viewModel.endCall() }

    val activeCall by viewModel.activeCall.collectAsState()
    if (activeCall == null) {
        LaunchedEffect(Unit) { onEndCall() }
        return
    }

    val call = activeCall!!
    val isConnected = call.state == CallState.CONNECTED
    val formattedDuration = remember(call.durationSeconds) {
        val minutes = call.durationSeconds / 60
        val seconds = call.durationSeconds % 60
        "%02d:%02d".format(minutes, seconds)
    }

    // Pulse animation for calling state
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09080E))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("call_screen_container")
    ) {
        // Video Stream Background (if Video Call and Camera On)
        if (call.type == CallType.VIDEO && call.isCameraOn) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = call.participant.avatarUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark cinematic vignette
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.7f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                // Local PiP (Picture in Picture) preview
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PureBlack,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, PureWhite.copy(alpha = 0.3f)),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 80.dp, end = 20.dp)
                        .size(width = 96.dp, height = 138.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = viewModel.currentUser.value.avatarUrl,
                            contentDescription = "Your Video",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { viewModel.flipCameraCall() },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cameraswitch,
                                contentDescription = "Flip",
                                tint = PureWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Voice Call Animated Centerpiece (when audio or video camera off)
        if (call.type == CallType.VOICE || !call.isCameraOn) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-40).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(170.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Pulsing Outer Rings
                    Box(
                        modifier = Modifier
                            .size(160.dp * if (!isConnected) pulseScale else 1.0f)
                            .clip(CircleShape)
                            .background(
                                if (call.participant.isCloseFriend) CloseFriendsGreen.copy(alpha = 0.15f)
                                else CirclePurple.copy(alpha = 0.15f)
                            )
                    )

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .border(
                                width = 3.dp,
                                color = if (call.participant.isCloseFriend) CloseFriendsGreen else CirclePurple,
                                shape = CircleShape
                            )
                            .padding(4.dp)
                    ) {
                        AsyncImage(
                            model = call.participant.avatarUrl,
                            contentDescription = call.participant.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = call.participant.name,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = PureWhite,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when (call.state) {
                        CallState.CALLING -> "Connecting via secure protocol..."
                        CallState.RINGING -> "Ringing..."
                        CallState.CONNECTED -> formattedDuration
                        CallState.ENDED -> "Call Ended"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (isConnected) CloseFriendsGreen else PureWhite.copy(alpha = 0.7f),
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        // Top Security & Call Header
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // High Level Security E2EE Protocol Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = PureBlack.copy(alpha = 0.75f),
                border = androidx.compose.foundation.BorderStroke(1.dp, CloseFriendsGreen.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        tint = CloseFriendsGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "End-to-End Encrypted (Curve25519)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PureWhite,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Key: ${call.encryptionFingerprint}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = PureWhite.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            )
        }

        // Bottom Call Controls Deck
        Surface(
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = PureBlack.copy(alpha = 0.85f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Audio Toggle
                    CallControlButton(
                        icon = if (call.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        label = if (call.isMuted) "Unmute" else "Mute",
                        isActive = call.isMuted,
                        onClick = { viewModel.toggleMuteCall() }
                    )

                    // Speaker Toggle
                    CallControlButton(
                        icon = if (call.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                        label = "Speaker",
                        isActive = call.isSpeakerOn,
                        onClick = { viewModel.toggleSpeakerCall() }
                    )

                    // Video Camera Toggle
                    CallControlButton(
                        icon = if (call.isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        label = if (call.isCameraOn) "Video On" else "Video Off",
                        isActive = call.isCameraOn,
                        onClick = { viewModel.toggleCameraCall() }
                    )

                    // End Call Button (Prominent Red)
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE53935))
                            .clickable { viewModel.endCall() }
                            .testTag("end_call_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = PureWhite,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CallControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(if (isActive) CirclePurple else Color.White.copy(alpha = 0.15f))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = PureWhite,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = PureWhite.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
        )
    }
}

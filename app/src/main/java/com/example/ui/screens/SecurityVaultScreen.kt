package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.CircleViewModel
import com.example.ui.Screen
import com.example.ui.theme.OrbynTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityVaultScreen(
    viewModel: CircleViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val colors = OrbynTheme.colors
    val colorScheme = MaterialTheme.colorScheme
    val settings by viewModel.securitySettings.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = colors.canvas,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = colors.closeFriendsAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Security & Privacy Vault",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.primaryText
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("security_vault_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.primaryText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.cardSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val securityScore = remember(settings) {
                var score = 0
                if (settings.isE2EEEnabled) score += 25
                if (settings.isScreenshotProtectionEnabled) score += 20
                if (settings.isAntiLeakWatermarkEnabled) score += 15
                if (settings.isAppLockEnabled) score += 20
                if (settings.isStrictInviteOnly) score += 10
                if (settings.ephemeralTimerMinutes != 0) score += 10
                score
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.closeFriendsContainer
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(colors.closeFriendsAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = colors.onCloseFriendsAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Security Shield: $securityScore%",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.closeFriendsAccent
                                    )
                                )
                                Text(
                                    text = if (securityScore >= 90) "Maximum Intimate Shield Active" else "Solid Protection Configured",
                                    style = MaterialTheme.typography.bodySmall.copy(color = colors.primaryText)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = colors.closeFriendsAccent.copy(alpha = 0.18f)
                        ) {
                            Text(
                                text = "VERIFIED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.closeFriendsAccent,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { securityScore / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = colors.closeFriendsAccent,
                        trackColor = colors.closeFriendsAccent.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = colors.closeFriendsAccent.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "CIPHER SUITE:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = colors.closeFriendsAccent,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = settings.encryptionProtocol,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = colors.primaryText
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "ACTIVE SAFETY FINGERPRINT:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = colors.closeFriendsAccent,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = settings.safetyFingerprint,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = colors.primaryText
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    var keysRotatedNotice by remember { mutableStateOf(false) }
                    Button(
                        onClick = {
                            viewModel.regenerateSecurityKeys()
                            keysRotatedNotice = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.closeFriendsAccent,
                            contentColor = colors.onCloseFriendsAccent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("rotate_security_keys_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (keysRotatedNotice) "New Session Keys Active ✓" else "Rotate Session Keys (Curve25519)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }

            Text(
                text = "ANTI-SURVEILLANCE & LEAK PREVENTION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = colors.secondaryText,
                    letterSpacing = 1.sp
                )
            )

            SecuritySwitchCard(
                title = "Screenshot & Recording Shield",
                subtitle = "Blocks OS screenshots and screen recorders when viewing private close friend moments.",
                checked = settings.isScreenshotProtectionEnabled,
                onCheckedChange = {
                    viewModel.updateSecuritySettings(settings.copy(isScreenshotProtectionEnabled = it))
                }
            )

            SecuritySwitchCard(
                title = "Cryptographic Watermarking",
                subtitle = "Subtly embeds invisible recipient signatures into media to trace unauthorized sharing.",
                checked = settings.isAntiLeakWatermarkEnabled,
                onCheckedChange = {
                    viewModel.updateSecuritySettings(settings.copy(isAntiLeakWatermarkEnabled = it))
                }
            )

            SecuritySwitchCard(
                title = "Biometric App Lock",
                subtitle = "Require Fingerprint or Face ID when switching back to Circle.",
                checked = settings.isAppLockEnabled,
                onCheckedChange = {
                    viewModel.updateSecuritySettings(settings.copy(isAppLockEnabled = it))
                }
            )

            Text(
                text = "EPHEMERAL MOMENT EXPIRATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = colors.secondaryText,
                    letterSpacing = 1.sp
                )
            )

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = colors.cardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Auto-Destruct Moments Timer",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = colors.primaryText
                    )
                    Text(
                        text = "Automatically shred photos & chat messages after set time.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.secondaryText
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val timers = listOf(
                        0 to "Keep Forever",
                        60 to "1 Hour",
                        1440 to "24 Hours (Default)",
                        -1 to "View Once 🔥"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        timers.forEach { (mins, label) ->
                            val isSelected = settings.ephemeralTimerMinutes == mins
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) colorScheme.primary else colors.elevatedSurface,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        viewModel.updateSecuritySettings(settings.copy(ephemeralTimerMinutes = mins))
                                    }
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) colorScheme.onPrimary else colors.primaryText
                                    ),
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colors.elevatedSurface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Zero Data Mining Guarantee",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = colors.primaryText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Circle has no advertising network, no tracking pixels, and no cloud automated analysis. Your private moments stay strictly between you and your close circle.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.secondaryText
                        )
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.closeFriendsAccent.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = colors.closeFriendsAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "On-Device Nudity & Abuse Guard",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = colors.primaryText
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Real-time client-side screening runs locally on your device prior to publishing. Prohibits explicit nudity, harassment, hate speech, and child exploitation without sending unencrypted media to any external server.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.secondaryText
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = colors.closeFriendsContainer
                        ) {
                            Text(
                                text = "✓ Nudity Filter Active",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = colors.closeFriendsAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = colors.closeFriendsContainer
                        ) {
                            Text(
                                text = "✓ Anti-Abuse Active",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = colors.closeFriendsAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "INTERACTION & NETWORK PRIVACY",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = colors.secondaryText,
                    letterSpacing = 1.sp
                )
            )

            SecuritySwitchCard(
                title = "Strict Invite-Only Circle",
                subtitle = "Only mutually approved close friends can see your moments, stories, or send chats. Blocks random accounts from viewing or finding you.",
                checked = settings.isStrictInviteOnly,
                onCheckedChange = {
                    viewModel.updateSecuritySettings(settings.copy(isStrictInviteOnly = it))
                }
            )

            SecuritySwitchCard(
                title = "Encrypted Peer-Relay Calling",
                subtitle = "Obfuscates your device IP address during voice and video calls through secure encrypted relay nodes.",
                checked = settings.isIncognitoCalling,
                onCheckedChange = {
                    viewModel.updateSecuritySettings(settings.copy(isIncognitoCalling = it))
                }
            )

            SecuritySwitchCard(
                title = "Incognito Read Receipts",
                subtitle = "Read incoming direct messages without notifying other participants or updating read status.",
                checked = settings.isHideReadReceipts,
                onCheckedChange = {
                    viewModel.updateSecuritySettings(settings.copy(isHideReadReceipts = it))
                }
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.elevatedSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(Screen.SupportBot) }
                    .testTag("security_support_bot_card")
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "Support Bot",
                            tint = colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Interactive FAQ & Support Bot",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = colors.primaryText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Get instant answers on Play Store age restrictions, family downloads, invite-only rules, and encryption.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.secondaryText
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = colorScheme.primary
                    )
                }
            }

            var showPrivacyDialog by remember { mutableStateOf(false) }
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPrivacyDialog = true }
                    .testTag("privacy_policy_card")
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(colors.closeFriendsContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = "Privacy & Disclaimer",
                            tint = colors.closeFriendsAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Privacy Policy & Legal Disclaimer",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = colors.primaryText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Review end-to-end encryption policy, zero telemetry commitment, and terms of service.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.secondaryText
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = colors.secondaryText
                    )
                }
            }

            if (showPrivacyDialog) {
                AlertDialog(
                    onDismissRequest = { showPrivacyDialog = false },
                    containerColor = colors.cardSurface,
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = colors.closeFriendsAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Privacy Policy & Disclaimer", style = MaterialTheme.typography.titleMedium, color = colors.primaryText)
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .heightIn(max = 360.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "1. Privacy Policy",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = colors.closeFriendsAccent)
                            )
                            Text(
                                text = "Circle is built from the ground up for intimate, private circles. All moments, chats, and stories are protected by cryptographic key exchange. We do not sell user data, maintain advertising trackers, or perform cloud profiling.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.primaryText
                            )
                            Text(
                                text = "2. On-Device Content Safety",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = colors.closeFriendsAccent)
                            )
                            Text(
                                text = "Content screening runs entirely on your local device hardware to comply with community standards and safety guidelines. No unencrypted media is harvested or stored on external analytics servers.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.primaryText
                            )
                            Text(
                                text = "3. Legal Disclaimer",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = colors.closeFriendsAccent)
                            )
                            Text(
                                text = "Circle is provided 'as is' without warranty of any kind. Users are solely responsible for content published within their invite-only circles. Ensure all participants in your circle are trusted acquaintances.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.primaryText
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showPrivacyDialog = false }) {
                            Text("Got It", color = colors.closeFriendsAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SecuritySwitchCard(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = OrbynTheme.colors

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = colors.cardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.primaryText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.secondaryText
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colors.onCloseFriendsAccent,
                    checkedTrackColor = colors.closeFriendsAccent
                )
            )
        }
    }
}

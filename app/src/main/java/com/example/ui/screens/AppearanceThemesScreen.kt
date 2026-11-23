package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.CircleViewModel
import com.example.ui.theme.AppTheme
import com.example.ui.theme.AppearanceMode
import com.example.ui.theme.OrbynPalettes
import com.example.ui.theme.OrbynTheme
import com.example.ui.theme.ThemeConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceThemesScreen(
    viewModel: CircleViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeConfig by viewModel.themeConfig.collectAsStateWithLifecycle()
    AppearanceThemesContent(
        themeConfig = themeConfig,
        onSelectTheme = { viewModel.setAppTheme(it) },
        onSelectAppearanceMode = { viewModel.setAppearanceMode(it) },
        onResetDefault = { viewModel.resetThemeToDefault() },
        onSaveAndApply = {
            viewModel.updateThemeConfig(themeConfig)
            onBack()
        },
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceThemesContent(
    themeConfig: ThemeConfig,
    onSelectTheme: (AppTheme) -> Unit,
    onSelectAppearanceMode: (AppearanceMode) -> Unit,
    onResetDefault: () -> Unit,
    onSaveAndApply: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val colors = OrbynTheme.colors
    val colorScheme = MaterialTheme.colorScheme
    val resolvedDark = themeConfig.resolveDarkTheme(isSystemInDarkTheme())
    val isDefaultConfig = themeConfig.theme == AppTheme.ROYAL_PURPLE &&
            themeConfig.appearanceMode == AppearanceMode.SYSTEM

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.canvas,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.appearance_and_themes_title),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.primaryText
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("appearance_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.primaryText
                        )
                    }
                },
                actions = {
                    if (!isDefaultConfig) {
                        TextButton(
                            onClick = onResetDefault,
                            modifier = Modifier.testTag("reset_theme_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.reset_default_theme),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.cardSurface
                )
            )
        },
        bottomBar = {
            Surface(
                color = colors.cardSurface,
                border = BorderStroke(1.dp, colors.divider),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = themeConfig.theme.displayName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = colors.primaryText
                        )
                        Text(
                            text = "${themeConfig.appearanceMode.displayName} mode • Active & saved",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.secondaryText
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = onSaveAndApply,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("apply_theme_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.apply_theme_button),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Section 1: Live Preview Card
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.live_preview_section_title),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = colors.secondaryText
                    )
                    LiveThemePreviewCard(
                        themeConfig = themeConfig,
                        resolvedDark = resolvedDark
                    )
                }

                // Section 2: Appearance Mode Selector (Light / Dark / System)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.appearance_mode_section_title),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = colors.secondaryText
                    )
                    AppearanceModeSelector(
                        selectedMode = themeConfig.appearanceMode,
                        onSelectMode = onSelectAppearanceMode
                    )
                }

                // Section 3: Six Selectable Theme Cards
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.theme_palette_section_title),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = colors.secondaryText
                        )
                        Text(
                            text = "${AppTheme.entries.size} themes",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.secondaryText
                        )
                    }

                    val themeRows = AppTheme.entries.chunked(2)
                    themeRows.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { appTheme ->
                                ThemePreviewCard(
                                    appTheme = appTheme,
                                    isSelected = themeConfig.theme == appTheme,
                                    previewDark = resolvedDark,
                                    onClick = { onSelectTheme(appTheme) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun LiveThemePreviewCard(
    themeConfig: ThemeConfig,
    resolvedDark: Boolean
) {
    val colors = OrbynTheme.colors
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = colors.cardSurface,
        border = BorderStroke(1.dp, colors.divider),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("live_theme_preview_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mini header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "orbyn",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp
                        ),
                        color = colors.primaryText
                    )
                    Text(
                        text = ".",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = colorScheme.primary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = colors.selectedContainer
                ) {
                    Text(
                        text = "${themeConfig.theme.displayName} • ${if (resolvedDark) "Dark" else "Light"}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        color = colors.onSelectedContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Mini thought/moment preview surface
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = colors.selectedContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Less noise. Closer circle.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.primaryText
                        )
                        Text(
                            text = "Cards, tabs, chats & controls update instantly",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.secondaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Mini controls row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = colorScheme.primary
                ) {
                    Text(
                        text = "Primary CTA",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = colors.closeFriendsContainer
                ) {
                    Text(
                        text = "Close friends",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.closeFriendsAccent,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Mini navigation tab indicator
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(width = 18.dp, height = 8.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primary)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(colors.divider)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(colors.divider)
                    )
                }
            }
        }
    }
}

@Composable
private fun AppearanceModeSelector(
    selectedMode: AppearanceMode,
    onSelectMode: (AppearanceMode) -> Unit
) {
    val colors = OrbynTheme.colors
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AppearanceMode.entries.forEach { mode ->
            val isSelected = selectedMode == mode
            val icon: ImageVector = when (mode) {
                AppearanceMode.LIGHT -> Icons.Default.LightMode
                AppearanceMode.DARK -> Icons.Default.DarkMode
                AppearanceMode.SYSTEM -> Icons.Default.SettingsBrightness
            }
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) colorScheme.primary else colors.divider,
                animationSpec = tween(180),
                label = "modeBorder"
            )
            val containerColor by animateColorAsState(
                targetValue = if (isSelected) colors.selectedContainer else colors.cardSurface,
                animationSpec = tween(180),
                label = "modeBg"
            )

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = containerColor,
                border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 76.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelectMode(mode) }
                    .testTag("appearance_mode_${mode.id}")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = mode.displayName,
                        tint = if (isSelected) colorScheme.primary else colors.secondaryText,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = mode.displayName,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) colors.onSelectedContainer else colors.primaryText,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemePreviewCard(
    appTheme: AppTheme,
    isSelected: Boolean,
    previewDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColors = OrbynTheme.colors
    val activeScheme = MaterialTheme.colorScheme

    val palette = remember(appTheme) { OrbynPalettes.forTheme(appTheme) }
    val previewScheme = remember(palette, previewDark) { palette.colorScheme(previewDark) }
    val previewExt = remember(palette, previewDark) { palette.extendedColors(previewDark) }

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) activeScheme.primary else activeColors.divider,
        animationSpec = tween(180),
        label = "themeCardBorder"
    )

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = activeColors.cardSurface,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("theme_card_${appTheme.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Mini UI Window Preview in that theme's exact Light/Dark palette
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(previewExt.canvas)
                    .border(1.dp, previewExt.divider, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Mini top row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(34.dp)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(previewExt.primaryText.copy(alpha = 0.85f))
                        )
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(previewScheme.primary)
                        )
                    }

                    // Mini card inside preview
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(previewExt.cardSurface)
                            .border(0.75.dp, previewExt.divider, RoundedCornerShape(8.dp))
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(previewExt.selectedContainer)
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.75f)
                                    .height(5.dp)
                                    .clip(CircleShape)
                                    .background(previewExt.primaryText.copy(alpha = 0.7f))
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .height(4.dp)
                                    .clip(CircleShape)
                                    .background(previewExt.secondaryText.copy(alpha = 0.55f))
                            )
                        }
                    }

                    // Mini accent pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(7.dp)
                                .clip(CircleShape)
                                .background(previewScheme.primary)
                        )
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .height(7.dp)
                                .clip(CircleShape)
                                .background(previewScheme.secondary)
                        )
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .height(7.dp)
                                .clip(CircleShape)
                                .background(previewExt.selectedContainer)
                        )
                    }
                }

                // Selected checkmark badge
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(activeScheme.primary)
                            .testTag("theme_selected_badge_${appTheme.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = activeScheme.onPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Theme Name, Default Badge & Swatch Dots
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = appTheme.displayName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = activeColors.primaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (appTheme.isDefault) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = activeColors.selectedContainer
                        ) {
                            Text(
                                text = "Default",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = activeColors.onSelectedContainer,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = appTheme.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = activeColors.secondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Colour Swatch Preview Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SwatchDot(color = previewScheme.primary, borderColor = activeColors.divider)
                    SwatchDot(color = previewScheme.secondary, borderColor = activeColors.divider)
                    SwatchDot(color = previewExt.selectedContainer, borderColor = activeColors.divider)
                    SwatchDot(color = previewExt.cardSurface, borderColor = activeColors.divider)
                }
            }
        }
    }
}

@Composable
private fun SwatchDot(
    color: Color,
    borderColor: Color
) {
    Box(
        modifier = Modifier
            .size(14.dp)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, borderColor, CircleShape)
    )
}

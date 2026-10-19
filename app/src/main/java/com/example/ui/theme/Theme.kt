package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = CirclePurple,
    onPrimary = PureWhite,
    primaryContainer = CirclePurpleContainer,
    onPrimaryContainer = CirclePurpleDark,
    secondary = CloseFriendsGreen,
    onSecondary = PureWhite,
    secondaryContainer = CloseFriendsGreenContainer,
    onSecondaryContainer = CloseFriendsGreen,
    background = NeutralBg,
    onBackground = PureBlack,
    surface = SurfaceCard,
    onSurface = PureBlack,
    surfaceVariant = DividerColor,
    onSurfaceVariant = SubtitleText,
    outline = BorderSubtle,
    outlineVariant = DividerColor
)

private val DarkColorScheme = darkColorScheme(
    primary = CirclePurpleLight,
    onPrimary = PureBlack,
    primaryContainer = DarkSurface,
    onPrimaryContainer = CirclePurpleLight,
    secondary = CloseFriendsGreen,
    onSecondary = PureBlack,
    secondaryContainer = DarkSurface,
    onSecondaryContainer = CloseFriendsGreen,
    background = DarkBg,
    onBackground = PureWhite,
    surface = DarkSurface,
    onSurface = PureWhite,
    surfaceVariant = DarkBorder,
    onSurfaceVariant = DarkMuted,
    outline = DarkBorder,
    outlineVariant = DarkBorder
)

@Composable
fun CircleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

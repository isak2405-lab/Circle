package com.example.ui.theme

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalOrbynColors = staticCompositionLocalOf<OrbynExtendedColors> {
    OrbynPalettes.forTheme(AppTheme.ROYAL_PURPLE).lightExtended
}

val LocalThemeConfig = staticCompositionLocalOf {
    ThemeConfig()
}

object OrbynTheme {
    val colors: OrbynExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalOrbynColors.current

    val config: ThemeConfig
        @Composable
        @ReadOnlyComposable
        get() = LocalThemeConfig.current
}

private const val THEME_TRANSITION_MS = 220

@Composable
private fun animateThemeColor(target: Color, label: String): Color {
    val animated by animateColorAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = THEME_TRANSITION_MS),
        label = label
    )
    return animated
}

@Composable
private fun animateColorScheme(target: ColorScheme): ColorScheme {
    return target.copy(
        primary = animateThemeColor(target.primary, "primary"),
        onPrimary = animateThemeColor(target.onPrimary, "onPrimary"),
        primaryContainer = animateThemeColor(target.primaryContainer, "primaryContainer"),
        onPrimaryContainer = animateThemeColor(target.onPrimaryContainer, "onPrimaryContainer"),
        secondary = animateThemeColor(target.secondary, "secondary"),
        onSecondary = animateThemeColor(target.onSecondary, "onSecondary"),
        secondaryContainer = animateThemeColor(target.secondaryContainer, "secondaryContainer"),
        onSecondaryContainer = animateThemeColor(target.onSecondaryContainer, "onSecondaryContainer"),
        background = animateThemeColor(target.background, "background"),
        onBackground = animateThemeColor(target.onBackground, "onBackground"),
        surface = animateThemeColor(target.surface, "surface"),
        onSurface = animateThemeColor(target.onSurface, "onSurface"),
        surfaceVariant = animateThemeColor(target.surfaceVariant, "surfaceVariant"),
        onSurfaceVariant = animateThemeColor(target.onSurfaceVariant, "onSurfaceVariant"),
        outline = animateThemeColor(target.outline, "outline"),
        outlineVariant = animateThemeColor(target.outlineVariant, "outlineVariant")
    )
}

@Composable
private fun animateExtendedColors(target: OrbynExtendedColors): OrbynExtendedColors {
    return target.copy(
        canvas = animateThemeColor(target.canvas, "canvas"),
        cardSurface = animateThemeColor(target.cardSurface, "cardSurface"),
        elevatedSurface = animateThemeColor(target.elevatedSurface, "elevatedSurface"),
        primaryText = animateThemeColor(target.primaryText, "primaryText"),
        secondaryText = animateThemeColor(target.secondaryText, "secondaryText"),
        placeholderText = animateThemeColor(target.placeholderText, "placeholderText"),
        divider = animateThemeColor(target.divider, "divider"),
        selectedContainer = animateThemeColor(target.selectedContainer, "selectedContainer"),
        onSelectedContainer = animateThemeColor(target.onSelectedContainer, "onSelectedContainer"),
        closeFriendsAccent = animateThemeColor(target.closeFriendsAccent, "closeFriendsAccent"),
        closeFriendsContainer = animateThemeColor(target.closeFriendsContainer, "closeFriendsContainer"),
        onCloseFriendsAccent = animateThemeColor(target.onCloseFriendsAccent, "onCloseFriendsAccent")
    )
}

@Composable
fun CircleTheme(
    themeConfig: ThemeConfig = ThemeConfig(),
    animateTransitions: Boolean = true,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val useDarkTheme = themeConfig.resolveDarkTheme(systemInDark)
    val palette = OrbynPalettes.forTheme(themeConfig.theme)

    val rawColorScheme = palette.colorScheme(useDarkTheme)
    val rawExtended = palette.extendedColors(useDarkTheme)

    val colorScheme = if (animateTransitions) animateColorScheme(rawColorScheme) else rawColorScheme
    val extendedColors = if (animateTransitions) animateExtendedColors(rawExtended) else rawExtended

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !useDarkTheme
            controller.isAppearanceLightNavigationBars = !useDarkTheme
        }
    }

    CompositionLocalProvider(
        LocalOrbynColors provides extendedColors,
        LocalThemeConfig provides themeConfig
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

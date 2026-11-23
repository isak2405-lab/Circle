package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

enum class AppTheme(
    val id: String,
    val displayName: String,
    val subtitle: String,
    val isDefault: Boolean = false
) {
    ROYAL_PURPLE(
        id = "royal_purple",
        displayName = "Royal Purple",
        subtitle = "Signature ORBYN identity",
        isDefault = true
    ),
    OCEAN_BLUE(
        id = "ocean_blue",
        displayName = "Ocean Blue",
        subtitle = "Clean coastal clarity"
    ),
    EMERALD_GREEN(
        id = "emerald_green",
        displayName = "Emerald Green",
        subtitle = "Calm botanical elegance"
    ),
    ROSE_PINK(
        id = "rose_pink",
        displayName = "Rose Pink",
        subtitle = "Modern soft rose"
    ),
    SUNSET_GOLD(
        id = "sunset_gold",
        displayName = "Sunset Gold",
        subtitle = "Warm golden hour"
    ),
    MIDNIGHT(
        id = "midnight",
        displayName = "Midnight",
        subtitle = "Premium obsidian depth"
    );

    companion object {
        fun fromId(id: String?): AppTheme =
            entries.firstOrNull { it.id == id } ?: ROYAL_PURPLE
    }
}

enum class AppearanceMode(
    val id: String,
    val displayName: String,
    val description: String
) {
    LIGHT(
        id = "light",
        displayName = "Light",
        description = "Clean bright surfaces"
    ),
    DARK(
        id = "dark",
        displayName = "Dark",
        description = "Comfortable low-light mode"
    ),
    SYSTEM(
        id = "system",
        displayName = "System",
        description = "Follows device appearance"
    );

    companion object {
        fun fromId(id: String?): AppearanceMode =
            entries.firstOrNull { it.id == id } ?: SYSTEM
    }
}

@Immutable
data class ThemeConfig(
    val theme: AppTheme = AppTheme.ROYAL_PURPLE,
    val appearanceMode: AppearanceMode = AppearanceMode.SYSTEM
) {
    fun resolveDarkTheme(isSystemInDarkTheme: Boolean): Boolean = when (appearanceMode) {
        AppearanceMode.LIGHT -> false
        AppearanceMode.DARK -> true
        AppearanceMode.SYSTEM -> isSystemInDarkTheme
    }
}

@Immutable
data class OrbynExtendedColors(
    val canvas: Color,
    val cardSurface: Color,
    val elevatedSurface: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val placeholderText: Color,
    val divider: Color,
    val selectedContainer: Color,
    val onSelectedContainer: Color,
    val closeFriendsAccent: Color,
    val closeFriendsContainer: Color,
    val onCloseFriendsAccent: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val mediaScrim: Color,
    val onMediaScrim: Color,
    val isDark: Boolean
)

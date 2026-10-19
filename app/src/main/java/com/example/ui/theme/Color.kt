package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Exact Color Tokens from Circle Companion Design System
val BrandPrimary = Color(0xFF6C5CE7)       // Main CTAs, active icons, unseen story rings, selected controls
val AppCanvas = Color(0xFFFAF9FC)          // Very light lavender-white background
val SurfaceColor = Color(0xFFFFFFFF)       // Cards, app bar, bottom navigation, sheets
val PrimaryText = Color(0xFF242329)        // Titles and body copy; near-black, not purple
val SecondaryText = Color(0xFF95919E)      // Timestamps, descriptions and inactive controls
val DividerOutline = Color(0xFFEEEDF2)     // Subtle one-pixel borders
val SelectedBackground = Color(0xFFF0EDFC) // Soft lavender selection fills
val PlaceholderHint = Color(0xFFA39EAC)    // Input hints

// Semantic aliases
val CirclePurple = BrandPrimary
val CirclePurpleContainer = SelectedBackground
val CloseFriendsGreen = Color(0xFF10B981)
val CloseFriendsGreenContainer = Color(0xFFE8F8F2)
val PureBlack = Color(0xFF0E0D14)
val PureWhite = Color(0xFFFFFFFF)

// Dark Theme support tokens (clean restrained dark)
val DarkCanvas = Color(0xFF0F0E17)
val DarkSurface = Color(0xFF171622)
val DarkTextPrimary = Color(0xFFF4F3F7)
val DarkTextSecondary = Color(0xFFA09DAA)
val DarkOutline = Color(0xFF262436)
val DarkSelected = Color(0xFF242038)

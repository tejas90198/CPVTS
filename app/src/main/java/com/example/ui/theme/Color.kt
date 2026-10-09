package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Web App Shared Design System: Exact Functional Color System
// Base Canvas & Navigation Surfaces
val CanvasWarm = Color(0xFFF9F7F2)
val NavSurfaceWarm = Color(0xFFF4EFE6)

// Cards & Surfaces
val CardSurfaceWhite = Color(0xFFFFFFFF)
val CardBorderWarm = Color(0xFFE6DFD5)

// Typography
val TextDarkCharcoal = Color(0xFF231F20)
val TextBronzeGray = Color(0xFF736B63)

// Tier 1 Functional Accent: Amber Gold (Primary Actions / Priority Tier 1)
val Tier1AmberGold = Color(0xFFFFB300)
val Tier1AmberGoldDark = Color(0xFFF57F17)
val Tier1CreamTint = Color(0xFFFFF8E1)

// Tier 2 Functional Accent: Navy / Slate Blue (Secondary Badges / Tier 2)
val Tier2SlateBlue = Color(0xFF1E88E5)
val Tier2SlateBlueDark = Color(0xFF1565C0)
val Tier2IceBlueTint = Color(0xFFE3F2FD)

// Tier 3 Functional Accent: Signal Forest Green (Active Status / Available / Tier 3)
val Tier3ForestGreen = Color(0xFF2E7D32)
val Tier3MintTint = Color(0xFFE8F5E9)

// High-Severity / Violation Citation Red
val CitationRed = Color(0xFFD32F2F)
val CitationRedTint = Color(0xFFFFEBEE)

// Compatibility aliases
val CampusNavyDark = TextDarkCharcoal
val CampusNavy = Tier2SlateBlueDark
val CampusNavyLight = Tier2SlateBlue
val AmberGold = Tier1AmberGold
val AmberGoldLight = Tier1AmberGold
val AmberGoldDark = Tier1AmberGoldDark
val TechCyan = Tier2SlateBlue
val StatusAvailable = Tier3ForestGreen
val StatusAvailableBg = Tier3MintTint
val StatusLimited = Tier1AmberGold
val StatusLimitedBg = Tier1CreamTint
val StatusFull = CitationRed
val StatusFullBg = CitationRedTint
val SurfaceLight = CanvasWarm
val SurfaceCardLight = CardSurfaceWhite
val SurfaceCardBorderLight = CardBorderWarm
val OnSurfaceLight = TextDarkCharcoal
val OnSurfaceSubtleLight = TextBronzeGray

package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// CPVTS Clean Light Mode Theme based on Web App Shared Design System
private val CpvtsColorScheme = lightColorScheme(
    primary = Tier2SlateBlueDark,
    onPrimary = Color.White,
    primaryContainer = Tier2IceBlueTint,
    onPrimaryContainer = Tier2SlateBlueDark,
    secondary = Tier1AmberGold,
    onSecondary = TextDarkCharcoal,
    secondaryContainer = Tier1CreamTint,
    onSecondaryContainer = Tier1AmberGoldDark,
    tertiary = Tier3ForestGreen,
    onTertiary = Color.White,
    tertiaryContainer = Tier3MintTint,
    onTertiaryContainer = Tier3ForestGreen,
    background = CanvasWarm,
    onBackground = TextDarkCharcoal,
    surface = CardSurfaceWhite,
    onSurface = TextDarkCharcoal,
    surfaceVariant = NavSurfaceWarm,
    onSurfaceVariant = TextBronzeGray,
    outline = CardBorderWarm,
    error = CitationRed,
    onError = Color.White,
    errorContainer = CitationRedTint,
    onErrorContainer = CitationRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Clean light mode interface per web design specification
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CpvtsColorScheme,
        typography = Typography,
        content = content
    )
}

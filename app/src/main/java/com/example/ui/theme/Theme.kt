package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SmartFinLightScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF1EB),
    onPrimaryContainer = Color(0xFF5D1F00),
    secondary = NavySecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2E7F0),
    onSecondaryContainer = NavyPrimary,
    tertiary = GoldAccent,
    onTertiary = NavyPrimary,
    background = BackgroundLight,
    onBackground = TextDark,
    surface = CardSurface,
    onSurface = TextDark,
    surfaceVariant = Color(0xFFEEF2F8),
    onSurfaceVariant = TextMuted,
    outline = LineBorder,
    error = StatusDelay,
    onError = Color.White
)

private val SmartFinDarkScheme = darkColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3E1B0A),
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = GoldAccent,
    onSecondary = NavyPrimary,
    secondaryContainer = NavySecondary,
    onSecondaryContainer = Color.White,
    tertiary = GoldAccent,
    onTertiary = NavyPrimary,
    background = NavyPrimary,
    onBackground = Color(0xFFE6E8EC),
    surface = NavySecondary,
    onSurface = Color(0xFFF4F6F9),
    surfaceVariant = NavyTertiary,
    onSurfaceVariant = Color(0xFFAEBBD0),
    outline = Color(0xFF23354E),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SmartFinDarkScheme else SmartFinLightScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

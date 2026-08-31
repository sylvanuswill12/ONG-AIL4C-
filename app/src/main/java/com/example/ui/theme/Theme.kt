package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val VibrantColorScheme = lightColorScheme(
    primary = AilEmerald, // Nature & ecological primary green
    onPrimary = Color.White,
    primaryContainer = AilEmeraldLight,
    onPrimaryContainer = AilEmeraldDark,
    secondary = AilOrangePrimary, // Lively warm orange accent
    onSecondary = Color.White,
    secondaryContainer = AilOrangeLight,
    onSecondaryContainer = AilOrangeDark,
    tertiary = AilAmber,
    onTertiary = Color.White,
    background = AilBackgroundLight,
    onBackground = AilOnSurfaceLight,
    surface = AilSurfaceLight,
    onSurface = AilOnSurfaceLight,
    surfaceVariant = AilSurfaceVariantLight,
    onSurfaceVariant = AilOnSurfaceMuted,
    outline = AilOutlineLight,
    error = Color(0xFFDC2626),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    // Keep vibrant, radiant, white & orange palette with green accents
    MaterialTheme(
        colorScheme = VibrantColorScheme,
        typography = Typography,
        content = content
    )
}

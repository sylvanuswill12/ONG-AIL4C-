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

private val DarkColorScheme = darkColorScheme(
    primary = AilEmerald,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF163E2C),
    onPrimaryContainer = AilMintLight,
    secondary = AilAmber,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF3B2713),
    onSecondaryContainer = AilSoftYellow,
    tertiary = AilBlueAccent,
    onTertiary = Color.White,
    background = AilBackgroundDark,
    onBackground = AilOnSurfaceDark,
    surface = AilSurfaceDark,
    onSurface = AilOnSurfaceDark,
    surfaceVariant = AilSurfaceVariantDark,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = AilOutlineDark,
    error = Color(0xFFEF4444),
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = AilEmerald,
    onPrimary = Color.White,
    primaryContainer = AilMintPillBg,
    onPrimaryContainer = AilMintDarkGreen,
    secondary = AilAmber,
    onSecondary = Color.White,
    secondaryContainer = AilSoftYellow,
    onSecondaryContainer = Color(0xFF92400E),
    tertiary = AilBlueAccent,
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
    dynamicColor: Boolean = false, // Keep branded eco identity
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

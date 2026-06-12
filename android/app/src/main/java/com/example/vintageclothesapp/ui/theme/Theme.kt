package com.example.vintageclothesapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VintageTeal = Color(0xFF00C9A7)
private val VintageTealDark = Color(0xFF009E82)
private val Black = Color(0xFF000000)
private val White = Color(0xFFFFFFFF)
private val DarkSurface = Color(0xFF1A1A1A)
private val LightSurface = Color(0xFFF5F5F5)

private val DarkColorScheme = darkColorScheme(
    primary = VintageTeal,
    onPrimary = Black,
    secondary = VintageTealDark,
    onSecondary = Black,
    background = Black,
    onBackground = White,
    surface = DarkSurface,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    primary = VintageTeal,
    onPrimary = Black,
    secondary = VintageTealDark,
    onSecondary = Black,
    background = White,
    onBackground = Black,
    surface = LightSurface,
    onSurface = Black
)

@Composable
fun VintageClothesAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
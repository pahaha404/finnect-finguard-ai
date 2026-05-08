package com.finnect.finguard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0D6B65),
    onPrimary = Color.White,
    secondary = Color(0xFF7A5C00),
    onSecondary = Color.White,
    tertiary = Color(0xFF485F7C),
    onTertiary = Color.White,
    background = Color(0xFFF7FAF9),
    onBackground = Color(0xFF17201F),
    surface = Color.White,
    onSurface = Color(0xFF17201F),
    surfaceVariant = Color(0xFFE1ECE9),
    onSurfaceVariant = Color(0xFF3F4E4B),
    error = Color(0xFFBA1A1A),
)

@Composable
fun FinGuardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content,
    )
}

package com.device.spec.extractor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Purple500,
    primaryContainer = Purple700,
    secondary = Teal200,
    secondaryContainer = Teal700,
    background = BackgroundColor,
    surface = SurfaceColor,
    error = ErrorColor,
    onPrimary = White,
    onSecondary = Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onError = White
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple200,
    primaryContainer = Purple500,
    secondary = Teal200,
    secondaryContainer = Teal700,
    background = Black,
    surface = SurfaceColor,
    error = ErrorColor,
    onPrimary = Black,
    onSecondary = Black,
    onBackground = White,
    onSurface = White,
    onError = White
)

@Composable
fun DeviceSpecExtractorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
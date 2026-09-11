package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val SampColorScheme =
  darkColorScheme(
    primary = SampBluePrimary,
    onPrimary = Color.White,
    primaryContainer = SampDarkSurface,
    onPrimaryContainer = SampBlueAccent,
    secondary = SampBlueSecondary,
    onSecondary = Color.Black,
    secondaryContainer = SampSurfaceCard,
    onSecondaryContainer = SampTextPrimary,
    tertiary = SampBlueAccent,
    onTertiary = Color.Black,
    background = SampDarkBg,
    onBackground = SampTextPrimary,
    surface = SampDarkSurface,
    onSurface = SampTextPrimary,
    surfaceVariant = SampSurfaceCard,
    onSurfaceVariant = SampTextSecondary,
    outline = SampBorderBlue,
    error = SampError,
    onError = Color.White
  )

@Composable
fun SampLauncherTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = SampColorScheme,
    typography = Typography,
    content = content
  )
}


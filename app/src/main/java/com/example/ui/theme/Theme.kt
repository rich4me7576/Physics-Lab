package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LabDarkColorScheme = darkColorScheme(
    primary = LabPhosphorGreen,
    onPrimary = Color(0xFF002912),
    primaryContainer = LabCardEmerald,
    onPrimaryContainer = LabTextPrimary,
    secondary = LabBrassGold,
    onSecondary = Color(0xFF2B2000),
    secondaryContainer = Color(0xFF3E3008),
    onSecondaryContainer = LabBrassGold,
    tertiary = LabLaserCyan,
    onTertiary = Color(0xFF002538),
    background = LabDeepSlate,
    onBackground = LabTextPrimary,
    surface = LabSurfaceDark,
    onSurface = LabTextPrimary,
    surfaceVariant = LabCardEmerald,
    onSurfaceVariant = LabTextSecondary,
    error = LabErrorCoral,
    onError = Color.White
)

@Composable
fun NaijaPhysLabTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LabDarkColorScheme,
        typography = Typography,
        content = content
    )
}

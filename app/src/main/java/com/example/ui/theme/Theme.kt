package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val AuraGlassColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = MidnightCanvas,
    primaryContainer = MidnightElevated,
    onPrimaryContainer = GlassTextPrimary,
    secondary = AuroraIndigo,
    onSecondary = MidnightCanvas,
    secondaryContainer = MidnightSurface,
    onSecondaryContainer = GlassTextPrimary,
    tertiary = AuroraMint,
    onTertiary = MidnightCanvas,
    background = MidnightCanvas,
    onBackground = GlassTextPrimary,
    surface = MidnightSurface,
    onSurface = GlassTextPrimary,
    surfaceVariant = MidnightElevated,
    onSurfaceVariant = GlassTextSecondary,
    outline = GlassBorderHighlight,
    outlineVariant = GlassBorderSubtle,
    error = CoralError,
    onError = MidnightCanvas,
    errorContainer = CoralContainer,
    onErrorContainer = GlassTextPrimary
)

private val AuraShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun AuraIdTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AuraGlassColorScheme,
        typography = Typography,
        shapes = AuraShapes,
        content = content
    )
}

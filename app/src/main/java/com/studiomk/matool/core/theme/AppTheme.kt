package com.studiomk.matool.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle

private val LightColors = lightColorScheme(
    primary = Color(0xFF007AFF),
    onPrimary = Color.White,
    primaryContainer = Color.White,
    onPrimaryContainer = Color(0xFF007AFF),
    secondary = Color(0xFF34C759),
    onSecondary = Color.White,
    onSecondaryContainer = Color(0xFF34C759),
    error = Color(0xFFFF3B30),
    background = Color(0xFFF2F2F7),
    surface = Color.White,
    onSurface = Color.Black
)

object HomeCardColors {
    val Red     = Color(0xFFED2326) // Soft Red
    val Blue    = Color(0xFF2860FF) // Soft Blue
    val Orange  = Color(0xFFF1820B) // Soft Orange
    val Yellow  = Color(0xFFF6C30A) // Soft Yellow
    val Green   = Color(0xFF39BF48) // Soft Green
}


fun Typography.scale(factor: Float): Typography {
    fun TextStyle.scaled() = this.copy(fontSize = this.fontSize * factor)

    return Typography(
        displayLarge = displayLarge.scaled(),
        displayMedium = displayMedium.scaled(),
        displaySmall = displaySmall.scaled(),
        headlineLarge = headlineLarge.scaled(),
        headlineMedium = headlineMedium.scaled(),
        headlineSmall = headlineSmall.scaled(),
        titleLarge = titleLarge.scaled(),
        titleMedium = titleMedium.scaled(),
        titleSmall = titleSmall.scaled(),
        bodyLarge = bodyLarge.scaled(),
        bodyMedium = bodyMedium.scaled(),
        bodySmall = bodySmall.scaled(),
        labelLarge = labelLarge.scaled(),
        labelMedium = labelMedium.scaled(),
        labelSmall = labelSmall.scaled()
    )
}
//private val DarkColors = darkColorScheme()

@Composable
fun AppTheme(
    useDarkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors =  LightColors
    val baseTypography = Typography() // or MaterialTheme.typography でもOK
    val scaledTypography = baseTypography.scale(1f) // 10%大きく
    MaterialTheme(
        colorScheme = colors,
        typography = scaledTypography,
        content = content
    )
}

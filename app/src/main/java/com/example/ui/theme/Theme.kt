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
    primary = FlameOrangeLight,
    onPrimary = Color.White,
    primaryContainer = FlameOrangeContainerDark,
    onPrimaryContainer = FlameOrangeContainer,
    secondary = WhatsAppGreen,
    onSecondary = Color.Black,
    secondaryContainer = WhatsAppGreenDark,
    onSecondaryContainer = Color.White,
    tertiary = AmberAccent,
    background = CharcoalDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceCardDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = FlameOrange,
    onPrimary = Color.White,
    primaryContainer = FlameOrangeContainer,
    onPrimaryContainer = FlameOrangeContainerDark,
    secondary = WhatsAppGreenDark,
    onSecondary = Color.White,
    secondaryContainer = WhatsAppGreenLight,
    onSecondaryContainer = WhatsAppGreenDark,
    tertiary = AmberAccent,
    background = SlateLight,
    surface = SurfaceLight,
    surfaceVariant = Color(0xFFEFF1F4),
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondarySecondaryLight(),
    error = ErrorRed
)

private fun TextSecondarySecondaryLight() = TextSecondaryLight

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded LPG flame colors consistent
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

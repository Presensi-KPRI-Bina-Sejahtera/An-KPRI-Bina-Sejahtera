package com.kpri.binasejahtera.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlack,
    onPrimary = Color.White,
    secondary = SecondaryGray,
    onSecondary = Color.White,
    tertiary = TertiaryGray,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorContainer,
    onErrorContainer = ErrorRed,
    background = AppBackground,
    onBackground = PrimaryBlack,
    surface = AppSurface,
    onSurface = PrimaryBlack,
    surfaceVariant = SuccessContainer,
    onSurfaceVariant = SuccessGreen
)

@Composable
fun KPRIBinasejahteraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // g ada dark mode jadinya light aja
    val colorScheme = if (darkTheme) LightColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
package com.skyfolk.quantoflife.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
        primary = Colors.Purple200,
        primaryVariant = Colors.Purple700,
        secondary = Colors.Teal200
)

private val LightColorPalette = lightColors(
        primary = Colors.Purple500,
        primaryVariant = Colors.Purple700,
        secondary = Colors.Teal200
)

@Composable
fun ComposeFlowTestTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content
    )
}
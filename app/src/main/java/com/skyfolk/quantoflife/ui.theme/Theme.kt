package com.skyfolk.quantoflife.ui.theme

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
//darkTheme: Boolean = isSystemInDarkTheme() -> LightColorPalette
fun QuantOfLifeMainTheme(content: @Composable() () -> Unit) {

    MaterialTheme(
            colors = DarkColorPalette,
            typography = Typography,
            shapes = Shapes,
            content = content
    )
}
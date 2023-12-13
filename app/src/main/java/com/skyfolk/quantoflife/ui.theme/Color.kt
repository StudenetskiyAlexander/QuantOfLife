package com.skyfolk.quantoflife.ui.theme

import androidx.compose.ui.graphics.Color
import android.graphics.Color as Color2

object Colors {
    val Red = Color(0xFFF00000)
    val Green = Color(0xFF00A637)
    val Purple200 = Color(0xFFBB86FC)
    val Purple500 = Color(0xFF6200EE)
    val Purple700 = Color(0xFF3700B3)
    val Teal200 = Color(0xFF03DAC5)
    val Orange = Color(0xFFFF8800)
    val White = Color(0xFFFFFFFF)
    val LightGray = Color(0xFF424242)
    val LightFontGray = Color(0xFFC6C6C6)
    val MidGray = Color(0xFF60635A)
}

fun Color.toInt(): Int {
    return Color2.rgb(this.red, this.green, this.blue)
}
package com.skyfolk.quantoflife.utils

import android.R
import android.graphics.Color
import android.util.Log
import java.lang.Exception
import java.util.*
import kotlin.math.roundToInt

object ColorTransparentUtils {
    // This default color int
    const val defaultColorID = R.color.black
    const val defaultColor = "000000"
    const val TAG = "ColorTransparentUtils"

    /**
     * This method convert numver into hexa number or we can say transparent code
     *
     * @param trans number of transparency you want
     * @return it return hex decimal number or transparency code
     */
    fun convert(trans: Int): String {
        val hexString = Integer.toHexString((255 * trans / 100).toFloat().roundToInt())
        return (if (hexString.length < 2) "0" else "") + hexString
    }


}

    fun transparentColor(colorCode: Int, trans: Int): Int {
        return convertIntoColor(colorCode, trans)
    }

fun convertIntoColor(colorCode: Int, transCode: Int): Int {
    var color = ColorTransparentUtils.defaultColor
    try {
        color = Integer.toHexString(colorCode).uppercase(Locale.getDefault()).substring(2)
    } catch (ignored: Exception) {
    }
    val colorString = if (!color.isEmpty() && transCode < 100) {
        if (color.trim { it <= ' ' }.length == 6) {
            "#" + ColorTransparentUtils.convert(transCode) + color
        } else {
            Log.d(ColorTransparentUtils.TAG, "Color is already with transparency")
            ColorTransparentUtils.convert(transCode) + color
        }
    } else "#" + Integer.toHexString(ColorTransparentUtils.defaultColorID)
        .uppercase(Locale.getDefault())
        .substring(2)

    return Color.parseColor(colorString)
    // if color is empty or any other problem occur then we return deafult color;
}
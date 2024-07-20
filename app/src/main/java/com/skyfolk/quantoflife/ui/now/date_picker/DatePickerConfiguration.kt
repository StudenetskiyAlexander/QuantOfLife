package com.skyfolk.quantoflife.ui.now.date_picker


import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class DatePickerConfiguration private constructor(
    val height: Dp,
    val headerHeight: Dp,
    val headerTextStyle: TextStyle,
    val headerArrowSize: Dp,
    val headerArrowColor: Color,
    val daysNameTextStyle: TextStyle,
    val dateTextStyle: TextStyle,
    val selectedDateTextStyle: TextStyle,
    val sundayTextColor: Color,
    val disabledDateColor: Color,
    val selectedDateBackgroundSize: Dp,
    val selectedDateBackgroundColor: Color,
    val selectedDateBackgroundShape: Shape,
    val monthYearTextStyle: TextStyle,
    val selectedMonthYearTextStyle: TextStyle,
    val selectedMonthYearScaleFactor: Float,
    val numberOfMonthYearRowsDisplayed: Int,
    val selectedMonthYearAreaHeight: Dp,
    val selectedMonthYearAreaColor: Color,
    val selectedMonthYearAreaShape: Shape
) {
    class Builder {
        private var height: Dp = DefaultDatePickerConfig.height
        private var headerHeight: Dp = DefaultDatePickerConfig.headerHeight
        private var headerTextStyle: TextStyle = DefaultDatePickerConfig.headerTextStyle
        private var headerArrowSize: Dp = DefaultDatePickerConfig.headerArrowSize
        private var headerArrowColor: Color = DefaultDatePickerConfig.headerArrowColor
        private var daysNameTextStyle: TextStyle = DefaultDatePickerConfig.daysNameTextStyle
        private var dateTextStyle: TextStyle = DefaultDatePickerConfig.dateTextStyle
        private var selectedDateTextStyle: TextStyle = DefaultDatePickerConfig.selectedDateTextStyle
        private var sundayTextColor: Color = DefaultDatePickerConfig.sundayTextColor
        private var disabledDateColor: Color = DefaultDatePickerConfig.disabledDateColor
        private var selectedDateBackgroundSize: Dp =
            DefaultDatePickerConfig.selectedDateBackgroundSize
        private var selectedDateBackgroundColor: Color =
            DefaultDatePickerConfig.selectedDateBackgroundColor
        private var selectedDateBackgroundShape: Shape =
            DefaultDatePickerConfig.selectedDateBackgroundShape
        private var monthYearTextStyle: TextStyle = DefaultDatePickerConfig.monthYearTextStyle
        private var selectedMonthYearTextStyle: TextStyle =
            DefaultDatePickerConfig.selectedMonthYearTextStyle
        private var numberOfMonthYearRowsDisplayed: Int =
            DefaultDatePickerConfig.numberOfMonthYearRowsDisplayed
        private var selectedMonthYearScaleFactor: Float =
            DefaultDatePickerConfig.selectedMonthYearScaleFactor
        private var selectedMonthYearAreaHeight: Dp =
            DefaultDatePickerConfig.selectedMonthYearAreaHeight
        private var selectedMonthYearAreaColor: Color =
            DefaultDatePickerConfig.selectedMonthYearAreaColor
        private var selectedMonthYearAreaShape: Shape =
            DefaultDatePickerConfig.selectedMonthYearAreaShape

        fun height(height: Dp) =
            apply { this.height = height }

        fun headerHeight(height: Dp) =
            apply { this.headerHeight = height }

        fun headerTextStyle(textStyle: TextStyle) =
            apply { this.headerTextStyle = textStyle }

        fun headerArrowSize(size: Dp) =
            apply { this.headerArrowSize = size }

        fun headerArrowColor(color: Color) =
            apply { this.headerArrowColor = color }

        fun daysNameTextStyle(textStyle: TextStyle) =
            apply { this.daysNameTextStyle = textStyle }

        fun dateTextStyle(textStyle: TextStyle) =
            apply { this.dateTextStyle = textStyle }

        fun selectedDateTextStyle(textStyle: TextStyle) =
            apply { this.selectedDateTextStyle = textStyle }

        fun sundayTextColor(color: Color) =
            apply { this.sundayTextColor = color }

        fun disabledDateColor(color: Color) =
            apply { this.disabledDateColor = color }

        fun selectedDateBackgroundSize(size: Dp) =
            apply { this.selectedDateBackgroundSize = size }

        fun selectedDateBackgroundColor(color: Color) =
            apply { this.selectedDateBackgroundColor = color }

        fun selectedDateBackgroundShape(shape: Shape) =
            apply { this.selectedDateBackgroundShape = shape }

        fun monthYearTextStyle(textStyle: TextStyle) =
            apply { this.monthYearTextStyle = textStyle }

        fun selectedMonthYearTextStyle(textStyle: TextStyle) =
            apply { this.selectedMonthYearTextStyle = textStyle }

        fun selectedMonthYearScaleFactor(scaleFactor: Float) =
            apply { this.selectedMonthYearScaleFactor = scaleFactor }

        fun numberOfMonthYearRowsDisplayed(count: Int) =
            apply { this.numberOfMonthYearRowsDisplayed = count }

        fun selectedMonthYearAreaHeight(height: Dp) =
            apply { this.selectedMonthYearAreaHeight = height }

        fun selectedMonthYearAreaColor(color: Color) =
            apply { this.selectedMonthYearAreaColor = color }

        fun selectedMonthYearAreaShape(shape: Shape) =
            apply { this.selectedMonthYearAreaShape = shape }

        fun build(): DatePickerConfiguration {
            return DatePickerConfiguration(
                height = height,
                headerHeight = headerHeight,
                headerTextStyle = headerTextStyle,
                headerArrowSize = headerArrowSize,
                headerArrowColor = headerArrowColor,
                daysNameTextStyle = daysNameTextStyle,
                dateTextStyle = dateTextStyle,
                selectedDateTextStyle = selectedDateTextStyle,
                sundayTextColor = sundayTextColor,
                disabledDateColor = disabledDateColor,
                selectedDateBackgroundSize = selectedDateBackgroundSize,
                selectedDateBackgroundColor = selectedDateBackgroundColor,
                selectedDateBackgroundShape = selectedDateBackgroundShape,
                monthYearTextStyle = monthYearTextStyle,
                selectedMonthYearTextStyle = selectedMonthYearTextStyle,
                selectedMonthYearScaleFactor = selectedMonthYearScaleFactor,
                numberOfMonthYearRowsDisplayed = numberOfMonthYearRowsDisplayed,
                selectedMonthYearAreaHeight = selectedMonthYearAreaHeight,
                selectedMonthYearAreaColor = selectedMonthYearAreaColor,
                selectedMonthYearAreaShape = selectedMonthYearAreaShape
            )
        }
    }
}


class DefaultDatePickerConfig private constructor() {

    companion object {
        val height: Dp = 260.dp

        // Header configuration
        val headerHeight: Dp = 35.dp
        val headerTextStyle: TextStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.W700,
            color = black
        )
        val headerArrowSize: Dp = 35.dp
        val headerArrowColor: Color = black

        // Date view configuration
        val daysNameTextStyle: TextStyle = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.W500,
            color = grayDark
        )
        val dateTextStyle: TextStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.W500,
            color = black
        )
        val selectedDateTextStyle: TextStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.W600,
            color = white
        )
        val sundayTextColor: Color = red
        val disabledDateColor: Color = grayLight
        val selectedDateBackgroundSize: Dp = 35.dp
        val selectedDateBackgroundColor: Color = blue
        val selectedDateBackgroundShape: Shape = CircleShape

        // Month Year view configuration
        val monthYearTextStyle: TextStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.W500,
            color = black.copy(alpha = 0.5f)
        )
        val selectedMonthYearTextStyle: TextStyle = TextStyle(
            fontSize = 17.sp,
            fontWeight = FontWeight.W600,
            color = black.copy(alpha = 1f)
        )
        const val numberOfMonthYearRowsDisplayed: Int = 7
        const val selectedMonthYearScaleFactor: Float = 1.2f
        val selectedMonthYearAreaHeight: Dp = 35.dp
        val selectedMonthYearAreaColor: Color = grayLight.copy(alpha = 0.2f)
        val selectedMonthYearAreaShape: Shape = RoundedCornerShape(Size.medium)
    }
}

val black = Color(0xFF333333)
val white = Color(0xFFFFFFFF)
val grayDark = Color(0xFF797979)
val red = Color(0xFFff0000)
val blue = Color(0xFF2979ff)
val grayLight = Color(0xFFaaaaaa)

object Size {
    val extraSmall = 2.dp
    val small = 4.dp
    val medium = 8.dp
    val large = 12.dp
    val extraLarge = 16.dp
}
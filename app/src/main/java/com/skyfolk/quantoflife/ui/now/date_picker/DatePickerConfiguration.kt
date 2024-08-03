package com.skyfolk.quantoflife.ui.now.date_picker


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class DatePickerConfiguration private constructor(
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
    companion object {
        @Composable
        fun builder(
            headerHeight: Dp = DefaultDatePickerConfig.headerHeight,
            headerTextStyle: TextStyle = DefaultDatePickerConfig.headerTextStyle(),
            headerArrowSize: Dp = DefaultDatePickerConfig.headerArrowSize,
            headerArrowColor: Color = DefaultDatePickerConfig.headerArrowColor(),
            daysNameTextStyle: TextStyle = DefaultDatePickerConfig.daysNameTextStyle,
            dateTextStyle: TextStyle = DefaultDatePickerConfig.dateTextStyle(),
            selectedDateTextStyle: TextStyle = DefaultDatePickerConfig.selectedDateTextStyle,
            sundayTextColor: Color = DefaultDatePickerConfig.sundayTextColor,
            disabledDateColor: Color = DefaultDatePickerConfig.disabledDateColor,
            selectedDateBackgroundSize: Dp =
                DefaultDatePickerConfig.selectedDateBackgroundSize,
            selectedDateBackgroundColor: Color =
                DefaultDatePickerConfig.selectedDateBackgroundColor,
            selectedDateBackgroundShape: Shape =
                DefaultDatePickerConfig.selectedDateBackgroundShape,
            monthYearTextStyle: TextStyle = DefaultDatePickerConfig.monthYearTextStyle(),
            selectedMonthYearTextStyle: TextStyle =
                DefaultDatePickerConfig.selectedMonthYearTextStyle(),
            numberOfMonthYearRowsDisplayed: Int =
                DefaultDatePickerConfig.numberOfMonthYearRowsDisplayed,
            selectedMonthYearScaleFactor: Float =
                DefaultDatePickerConfig.selectedMonthYearScaleFactor,
            selectedMonthYearAreaHeight: Dp =
                DefaultDatePickerConfig.selectedMonthYearAreaHeight,
            selectedMonthYearAreaColor: Color =
                DefaultDatePickerConfig.selectedMonthYearAreaColor,
            selectedMonthYearAreaShape: Shape =
                DefaultDatePickerConfig.selectedMonthYearAreaShape
        ) = DatePickerConfiguration(
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


class DefaultDatePickerConfig private constructor() {

    companion object {
        val timePickerHeight = 300.dp
        val monthHeight = 220.dp

        // Header configuration
        val headerHeight: Dp = 35.dp

        @Composable
        fun headerTextStyle(): TextStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.W700,
            color = black()
        )

        val headerArrowSize: Dp = 35.dp

        @Composable
        fun headerArrowColor(): Color = black()

        // Date view configuration
        val daysNameTextStyle: TextStyle = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.W500,
            color = grayDark
        )

        @Composable
        fun dateTextStyle(): TextStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.W500,
            color = black()
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
        @Composable
        fun monthYearTextStyle(): TextStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.W500,
            color = black().copy(alpha = 0.5f)
        )

        @Composable
        fun selectedMonthYearTextStyle(): TextStyle = TextStyle(
            fontSize = 17.sp,
            fontWeight = FontWeight.W600,
            color = black().copy(alpha = 1f)
        )

        const val numberOfMonthYearRowsDisplayed: Int = 7
        const val selectedMonthYearScaleFactor: Float = 1.2f
        val selectedMonthYearAreaHeight: Dp = 35.dp
        val selectedMonthYearAreaColor: Color = grayLight.copy(alpha = 0.2f)
        val selectedMonthYearAreaShape: Shape = RoundedCornerShape(Size.medium)
    }
}

@Composable
fun black() = when (isSystemInDarkTheme()) {
    true -> Color(0xFFFFFFFF)
    false -> Color(0xFF333333)
}

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
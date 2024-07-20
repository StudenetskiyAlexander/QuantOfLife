package com.skyfolk.quantoflife.ui.now.date_picker

import android.icu.util.Calendar

data class DatePickerDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int
)

object DefaultDate {
    private val calendar = Calendar.getInstance()
    val defaultDate = DatePickerDate(
        calendar[Calendar.YEAR],
        calendar[Calendar.MONTH],
        calendar[Calendar.DAY_OF_MONTH],
        calendar[Calendar.HOUR],
        calendar[Calendar.MINUTE]
    )
}
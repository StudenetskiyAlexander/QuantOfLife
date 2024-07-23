package com.skyfolk.quantoflife.ui.now.date_picker

import java.util.Calendar


internal object Constant {
    private const val repeatCount: Int = 200

    val days = listOf(
        Days.MONDAY,
        Days.TUESDAY,
        Days.WEDNESDAY,
        Days.THURSDAY,
        Days.FRIDAY,
        Days.SATURDAY,
        Days.SUNDAY
    )

    private val monthNames = listOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December",
    )

    fun getMonths(): List<String> {
        val list = mutableListOf<String>()
        for (i in 1..repeatCount) {
            list.addAll(monthNames)
        }
        return list
    }

    fun getMiddleOfMonth(): Int {
        return 12 * (repeatCount / 2)
    }

    val years = List(200) { it + Calendar.getInstance()[Calendar.YEAR] - 100 }
}
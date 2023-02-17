package com.skyfolk.quantoflife.mapper

import com.skyfolk.quantoflife.ui.entity.GraphSelectedYearMode
import com.skyfolk.quantoflife.IDateTimeRepository
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.utils.getEndDateCalendar
import java.util.*
import kotlin.math.min

class GraphSelectedYearToLastTimeMapper(
    private val dateTimeRepository: IDateTimeRepository
) {

    fun invoke(year: GraphSelectedYearMode, startDayTime: Long): Long {
        val lastDate = when (year) {
            GraphSelectedYearMode.All -> dateTimeRepository.getTimeInMillis()
            is GraphSelectedYearMode.OnlyYearMode -> {
                val calendar = dateTimeRepository.getCalendar()
                calendar[Calendar.YEAR] = year.year
                calendar
                    .getEndDateCalendar(TimeInterval.Year, startDayTime)
                    .timeInMillis
            }
        }
        return min(lastDate, dateTimeRepository.getTimeInMillis())
    }
}
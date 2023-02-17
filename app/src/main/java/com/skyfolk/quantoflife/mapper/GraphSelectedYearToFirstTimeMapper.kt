package com.skyfolk.quantoflife.mapper

import com.skyfolk.quantoflife.ui.entity.GraphSelectedYearMode
import com.skyfolk.quantoflife.IDateTimeRepository
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.utils.getStartDateCalendar
import java.util.*

class GraphSelectedYearToFirstTimeMapper(
    private val dateTimeRepository: IDateTimeRepository
) {

    fun invoke(year: GraphSelectedYearMode, startDayTime: Long, firstEventDate: Long): Long {
        return when (year) {
            GraphSelectedYearMode.All -> firstEventDate
            is GraphSelectedYearMode.OnlyYearMode -> {
                val calendar = dateTimeRepository.getCalendar()
                calendar[Calendar.YEAR] = year.year
                calendar
                    .getStartDateCalendar(TimeInterval.Year, startDayTime)
                    .timeInMillis
            }
        }
    }
}
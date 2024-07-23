package com.skyfolk.quantoflife.mapper

import com.skyfolk.quantoflife.IDateTimeRepository
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.utils.getEndDateCalendar
import com.skyfolk.quantoflife.utils.getStartDateCalendar
import java.util.Calendar

class TimeIntervalToPeriodInMillisMapper(
    private val dateTimeRepository: IDateTimeRepository
) {

    fun invoke(
        timeInterval: TimeInterval,
        startDayTime: Long,
        offset: Calendar? = null
    ): LongRange {

        val calendar = when (offset) {
            null -> dateTimeRepository.getCalendar()
            else -> offset
        }
        return LongRange(
            calendar.getStartDateCalendar(
                timeInterval,
                startDayTime
            ).timeInMillis,
            calendar.getEndDateCalendar(
                timeInterval,
                startDayTime
            ).timeInMillis
        )
    }
}
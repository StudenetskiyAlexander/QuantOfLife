package com.skyfolk.quantoflife.mapper

import com.skyfolk.quantoflife.IDateTimeRepository
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.utils.getEndDateCalendar
import com.skyfolk.quantoflife.utils.getStartDateCalendar

class TimeIntervalToPeriodInMillisMapper(
    private val dateTimeRepository: IDateTimeRepository
) : (TimeInterval, Long) -> LongRange {

    override fun invoke(timeInterval: TimeInterval, startDayTime: Long): LongRange {

        return LongRange(
            dateTimeRepository.getCalendar().getStartDateCalendar(
                timeInterval,
                startDayTime
            ).timeInMillis,
            dateTimeRepository.getCalendar().getEndDateCalendar(
                timeInterval,
                startDayTime
            ).timeInMillis
        )
    }
}
package com.skyfolk.quantoflife.timeInterval

import com.skyfolk.quantoflife.TypedSealedClass
import com.skyfolk.quantoflife.utils.getStartDateCalendar
import java.util.*

sealed class TimeInterval : TypedSealedClass {
    override val type = this.javaClass.name

    object Today : TimeInterval()
    object Week : TimeInterval()
    object Month : TimeInterval()
    object Year : TimeInterval()
    data class Selected(val start: Long, val end: Long) : TimeInterval()
    object All : TimeInterval()

    companion object {
        fun toTimeInterval(enumString: String, start: Long, end: Long): TimeInterval {
            return when (enumString) {
                Today.javaClass.name -> Today
                Week.javaClass.name -> Week
                Month.javaClass.name -> Month
                Selected(start, end).javaClass.name -> Selected(start, end)
                else -> All
            }
        }
    }

    fun getPeriod(firstDate: Long, index: Int, startDayTime: Long): LongRange {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = firstDate
        calendar.timeInMillis = calendar.getStartDateCalendar(
            this,
            startDayTime
        ).timeInMillis
        calendar.add(this.toCalendarPeriod(), index + 1)

        val startFirstPeriodTimeInMillis =
            calendar.getStartDateCalendar(
                this,
                startDayTime
            ).timeInMillis

        calendar.timeInMillis = startFirstPeriodTimeInMillis

        calendar[this.toCalendarPeriod()] += 1
        calendar.timeInMillis -= 24 * 60 * 60 * 1000

        return LongRange(startFirstPeriodTimeInMillis, calendar.timeInMillis)
    }

    private fun toCalendarPeriod(): Int {
        return when (this) {
            Week -> Calendar.WEEK_OF_YEAR
            Month -> Calendar.MONTH
            Today -> Calendar.DAY_OF_YEAR
            else -> Calendar.DAY_OF_YEAR
        }
    }

    fun toGraphPosition(): Int {
        return when (this) {
            is Today -> 0
            is Week -> 1
            else -> 2
        }
    }

    fun toStringName(array: Array<String>): String {
        return when (this) {
            is Today -> array[0]
            is Week -> array[1]
            is Month -> array[2]
            is All -> array[3]
            is Selected -> array[4]
            is Year -> array[5]
        }
    }
}

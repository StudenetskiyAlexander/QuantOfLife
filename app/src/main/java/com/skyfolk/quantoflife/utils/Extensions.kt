package com.skyfolk.quantoflife.utils

import com.google.android.material.snackbar.Snackbar
import com.skyfolk.quantoflife.entity.QuantBase
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import java.util.*
import kotlin.collections.ArrayList

fun Snackbar.setOnHideByTimeout(onTimeout: () -> Unit) {
    this.addCallback(object : Snackbar.Callback() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)
            if (event == 2) {
                onTimeout()
            }
        }
    })
}

fun List<QuantBase>.filterToArrayList(predicate: (QuantBase) -> Boolean): ArrayList<QuantBase> {
    return ArrayList(this.filter(predicate))
}

fun Calendar.lessHourAndMinute(calendar: Calendar): Boolean {
    if ((this[Calendar.HOUR_OF_DAY] < calendar[Calendar.HOUR_OF_DAY]) ||
        (this[Calendar.HOUR_OF_DAY] == calendar[Calendar.HOUR_OF_DAY] && this.timeInMillis < calendar.timeInMillis)
    ) {
        return true
    }
    return false
}

fun Calendar.moreHourAndMinute(calendar: Calendar): Boolean {
    if ((this[Calendar.HOUR_OF_DAY] > calendar[Calendar.HOUR_OF_DAY]) ||
        (this[Calendar.HOUR_OF_DAY] == calendar[Calendar.HOUR_OF_DAY] && this.timeInMillis > calendar.timeInMillis)
    ) {
        return true
    }
    return false
}

//Возвращает начало временного интервала timeInterval,
//учитывая, что сутки начинаются в startDayTime миллисекунд, а не в полночь
fun Calendar.getStartDateCalendar(timeInterval: TimeInterval, startDayTime: Long): Calendar {
    val calendar: Calendar = this.clone() as Calendar
    when (timeInterval) {
        is TimeInterval.All -> {
            calendar[Calendar.YEAR] = 1900
        }
        is TimeInterval.Month -> {
            if (calendar[Calendar.DAY_OF_MONTH] == 1 && calendar.lessHourAndMinute(startDayTime.toCalendar())) {
                calendar[Calendar.MONTH]--
            }
            calendar[Calendar.DAY_OF_MONTH] = 1
        }
        is TimeInterval.Week -> {
            if (calendar[Calendar.DAY_OF_WEEK] == 2 && calendar.lessHourAndMinute(startDayTime.toCalendar())) {
                calendar[Calendar.WEEK_OF_YEAR]--
            }
            calendar[Calendar.DAY_OF_WEEK] = 2
        }
        is TimeInterval.Today -> {
            if (calendar.lessHourAndMinute(startDayTime.toCalendarOnlyHourAndMinute())) {
                calendar[Calendar.DAY_OF_MONTH]--
            }
        }
        is TimeInterval.Selected -> {
            calendar.timeInMillis = timeInterval.start
        }
        is TimeInterval.Year -> {
            calendar[Calendar.DAY_OF_YEAR] = 1
        }
    }
    calendar[Calendar.HOUR_OF_DAY] =
        startDayTime.toCalendarOnlyHourAndMinute()[Calendar.HOUR_OF_DAY]
    calendar[Calendar.MINUTE] = startDayTime.toCalendarOnlyHourAndMinute()[Calendar.MINUTE]
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    return calendar
}

//Возвращает окончание временного интервала timeInterval,
//учитывая, что сутки начинаются в startDayTime миллисекунд, а не в полночь
fun Calendar.getEndDateCalendar(timeInterval: TimeInterval, startDayTime: Long): Calendar {
    val calendar: Calendar = this.clone() as Calendar
    calendar.minimalDaysInFirstWeek = 1

    when (timeInterval) {
        is TimeInterval.All -> {
            calendar[Calendar.YEAR] = 30900
        }
        is TimeInterval.Month -> {
            if (calendar[Calendar.DAY_OF_MONTH] > 1 ||
                (calendar[Calendar.DAY_OF_MONTH] == 1 && calendar.moreHourAndMinute(startDayTime.toCalendarOnlyHourAndMinute()))
            ) {
                calendar[Calendar.MONTH]++
            }
            calendar[Calendar.DAY_OF_MONTH] = 1
        }
        is TimeInterval.Week -> {
            if (calendar[Calendar.DAY_OF_WEEK] > 2 ||
                (calendar[Calendar.DAY_OF_WEEK] == 2 && calendar.moreHourAndMinute(startDayTime.toCalendarOnlyHourAndMinute())) ||
                calendar[Calendar.DAY_OF_WEEK] == 1
            ) {
                calendar.timeInMillis += 7 * 24 * 60 * 60 * 1000
            }
            calendar[Calendar.DAY_OF_WEEK] = 2
        }
        is TimeInterval.Today -> {
            if (calendar.moreHourAndMinute(startDayTime.toCalendarOnlyHourAndMinute())) {
                calendar[Calendar.DAY_OF_MONTH]++
            }
        }
        is TimeInterval.Selected -> {
            calendar.timeInMillis = timeInterval.end
            calendar[Calendar.DAY_OF_MONTH]++
        }
        is TimeInterval.Year -> {
            calendar[Calendar.YEAR]++
            calendar[Calendar.DAY_OF_YEAR] = 1
        }
    }
    calendar[Calendar.HOUR_OF_DAY] =
        startDayTime.toCalendarOnlyHourAndMinute()[Calendar.HOUR_OF_DAY]
    calendar[Calendar.MINUTE] = startDayTime.toCalendarOnlyHourAndMinute()[Calendar.MINUTE]
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    return calendar
}
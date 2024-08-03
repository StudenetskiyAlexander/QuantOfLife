package com.skyfolk.quantoflife.ui.feeds.view

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.entity.QuantFilterMode
import com.skyfolk.quantoflife.ui.now.SelectDateTimeFragment
import java.util.Calendar

class QuantFilterToContentMapper : (QuantFilterMode) -> String {
    override fun invoke(quantFilterMode: QuantFilterMode): String = when (quantFilterMode) {
        is QuantFilterMode.All -> "Все события"
        is QuantFilterMode.OnlySelected -> quantFilterMode.quant.name
    }
}

class TimeIntervalToContentMapper : (TimeInterval) -> String {
    override fun invoke(timeInterval: TimeInterval): String = when (timeInterval) {
        is TimeInterval.All -> "Все время"
        is TimeInterval.Month -> "Месяц"
        is TimeInterval.Selected -> "Выбранный интервал"
        is TimeInterval.Today -> "Сегодня"
        is TimeInterval.Week -> "Неделя"
        is TimeInterval.Year -> "Год"
    }
}


@Composable
fun SelectedTimeInterval(
    context: Context,
    intervalStart: Calendar,
    intervalEnd: Calendar,
    setTimeInterval: (TimeInterval.Selected) -> Unit,
) {
    val dayInMillis = 24 * 60 * 60 * 1000

    fun onStartDateSelected(calendar: Calendar) {
        intervalStart.set(Calendar.YEAR, calendar[Calendar.YEAR])
        intervalStart.set(Calendar.MONTH, calendar[Calendar.MONTH])
        intervalStart.set(Calendar.DAY_OF_MONTH, calendar[Calendar.DAY_OF_MONTH])

        if (intervalEnd.timeInMillis < intervalStart.timeInMillis) {
            intervalEnd.timeInMillis = intervalStart.timeInMillis + dayInMillis
        }

        setTimeInterval(
            TimeInterval.Selected(
                intervalStart.timeInMillis,
                intervalEnd.timeInMillis
            )
        )
    }

    fun onEndDateSelected(calendar: Calendar) {
        intervalEnd.set(Calendar.YEAR, calendar[Calendar.YEAR])
        intervalEnd.set(Calendar.MONTH, calendar[Calendar.MONTH])
        intervalEnd.set(Calendar.DAY_OF_MONTH, calendar[Calendar.DAY_OF_MONTH])

        if (intervalEnd.timeInMillis < intervalStart.timeInMillis) {
            intervalStart.timeInMillis = intervalEnd.timeInMillis - dayInMillis
        }

        setTimeInterval(
            TimeInterval.Selected(
                intervalStart.timeInMillis,
                intervalEnd.timeInMillis
            )
        )
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 5.dp)
    ) {
        TimeSelectorView(
            time = intervalStart.timeInMillis,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            val dialog = SelectDateTimeFragment(intervalStart, true) {
                it?.let { onStartDateSelected(it) }
            }
            val fm: FragmentManager = (context as FragmentActivity).supportFragmentManager
            dialog.show(fm, dialog.tag)
        }
        TimeSelectorView(
            time = intervalEnd.timeInMillis,
            horizontalArrangement = Arrangement.Start
        ) {
            val dialog = SelectDateTimeFragment(intervalStart, true) {
                it?.let { onEndDateSelected(it) }
            }
            val fm: FragmentManager = (context as FragmentActivity).supportFragmentManager
            dialog.show(fm, dialog.tag)
        }
    }
}
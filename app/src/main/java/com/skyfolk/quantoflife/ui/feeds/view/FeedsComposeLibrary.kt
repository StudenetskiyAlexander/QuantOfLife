package com.skyfolk.quantoflife.ui.feeds.view

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.skyfolk.quantoflife.R
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.entity.QuantFilterMode
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

    val onStartDateSelected =
        DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
            intervalStart.set(Calendar.YEAR, year)
            intervalStart.set(Calendar.MONTH, month)
            intervalStart.set(Calendar.DAY_OF_MONTH, day)

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

    val onEndDateSelected =
        DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
            intervalEnd.set(Calendar.YEAR, year)
            intervalEnd.set(Calendar.MONTH, month)
            intervalEnd.set(Calendar.DAY_OF_MONTH, day)

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
            DatePickerDialog(
                context,
                onStartDateSelected,
                intervalStart.get(Calendar.YEAR),
                intervalStart.get(
                    Calendar.MONTH
                ),
                intervalStart.get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }
        TimeSelectorView(
            time = intervalEnd.timeInMillis,
            horizontalArrangement = Arrangement.Start
        ) {
            DatePickerDialog(
                context,
                onEndDateSelected,
                intervalEnd.get(Calendar.YEAR),
                intervalEnd.get(
                    Calendar.MONTH
                ),
                intervalEnd.get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }
    }
}
package com.skyfolk.quantoflife.ui.now.date_picker

import EventOnPicker
import TimePicker
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyfolk.quantoflife.DateTimeRepository
import com.skyfolk.quantoflife.IDateTimeRepository
import com.skyfolk.quantoflife.mapper.QuantBaseToCreateQuantTypeMapper
import com.skyfolk.quantoflife.mapper.TimeIntervalToPeriodInMillisMapper
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.goals.GoalToPresentationMapper
import com.skyfolk.quantoflife.ui.now.date_picker.DefaultDatePickerConfig.Companion.height
import com.skyfolk.quantoflife.ui.now.date_picker.Size.medium
import getTimeWithZeroText
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication
import java.util.Calendar
import java.util.Locale
import org.koin.compose.koinInject
import org.koin.dsl.module
import kotlin.math.ceil

@Composable
fun DateTimePicker(
    timeIntervalToPeriodInMillisMapper: TimeIntervalToPeriodInMillisMapper = koinInject(),
    onDateSelected: (Calendar?) -> Unit,
    events: List<EventOnPicker>,
    startDate: Calendar = Calendar.getInstance(),
    configuration: DatePickerConfiguration = DatePickerConfiguration.builder(),
) {
    var isMonthYearPickerVisible by remember { mutableStateOf(false) }
    var currentDate by remember { mutableStateOf(startDate) }
    var selectedDate by remember { mutableStateOf(startDate) }

    Box {
        Column {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp),
                textAlign = TextAlign.Center,
                text = "${selectedDate[Calendar.DAY_OF_MONTH]} ${
                    selectedDate.getDisplayName(
                        Calendar.MONTH,
                        Calendar.LONG,
                        Locale.getDefault()
                    )
                } ${selectedDate[Calendar.YEAR]}, ${
                    getTimeWithZeroText(
                        selectedDate,
                        Calendar.HOUR_OF_DAY
                    )
                }:${getTimeWithZeroText(selectedDate, Calendar.MINUTE)}",
                style = configuration.headerTextStyle
            )
            CalendarHeader(
                title = "${
                    currentDate.getDisplayName(
                        Calendar.MONTH,
                        Calendar.LONG,
                        Locale.getDefault()
                    )
                } ${currentDate[Calendar.YEAR]} ▼",
                onMonthYearClick = { isMonthYearPickerVisible = true },
                onNextClick = {
                    val tmp = Calendar.getInstance()
                    tmp.time = currentDate.time
                    tmp.add(Calendar.MONTH, 1)
                    currentDate = tmp
                },
                onPreviousClick = {
                    val tmp = Calendar.getInstance()
                    tmp.time = currentDate.time
                    tmp.add(Calendar.MONTH, -1)
                    currentDate = tmp
                },
                configuration = configuration,
            )

            Box(
                modifier = Modifier
                    .height(height)
            ) {
                AnimatedFadeVisibility(
                    visible = !isMonthYearPickerVisible
                ) {
                    Column {
                        DateView(
                            modifier = Modifier.padding(bottom = 10.dp),
                            currentDate = currentDate,
                            selectedDate = selectedDate,
                            events = events,
                            timeIntervalToPeriodInMillisMapper = timeIntervalToPeriodInMillisMapper,
                            onDaySelected = {
                                val tmp = selectedDate.copy()
                                tmp[Calendar.YEAR] = it[Calendar.YEAR]
                                tmp[Calendar.MONTH] = it[Calendar.MONTH]
                                tmp[Calendar.DAY_OF_MONTH] = it[Calendar.DAY_OF_MONTH]
                                selectedDate = tmp
                            },
                            height = 100.dp
                        )

                        TimePicker(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            events = events.filter {
                                it.time in timeIntervalToPeriodInMillisMapper.invoke(
                                    TimeInterval.Today,
                                    0,
                                    selectedDate
                                )
                            },
                            initialTimeInMinutes = currentDate[Calendar.HOUR_OF_DAY] * 60 + currentDate[Calendar.MINUTE]
                        ) {
                            val tmp = Calendar.getInstance()
                            tmp.time = selectedDate.time
                            tmp[Calendar.HOUR_OF_DAY] = it / 60
                            tmp[Calendar.MINUTE] = it % 60
                            selectedDate = tmp
                        }
                    }
                }
                AnimatedFadeVisibility(
                    visible = isMonthYearPickerVisible
                ) {
                    MonthAndYearView(
                        modifier = Modifier.align(Alignment.Center),
                        selectedDate = currentDate,
                        onMonthChange = {
                            val tmp = Calendar.getInstance()
                            tmp.time = currentDate.time
                            tmp[Calendar.MONTH] = it
                            currentDate = tmp
                            isMonthYearPickerVisible = false
                        },
                        onYearChange = {
                            val tmp = Calendar.getInstance()
                            tmp.time = currentDate.time
                            tmp[Calendar.YEAR] = it + Calendar.getInstance()[Calendar.YEAR] - 20
                            currentDate = tmp
                        },
                        years = List(40) { (it + Calendar.getInstance()[Calendar.YEAR] - 20).toString() },
                        months = List(12) {
                            val tmp = Calendar.getInstance()
                            tmp[Calendar.MONTH] = it
                            tmp.getDisplayName(
                                Calendar.MONTH,
                                Calendar.LONG,
                                Locale.getDefault()
                            ) ?: ""
                        },
                        height = height,
                        configuration = configuration
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onDateSelected(null) }) {
                    Text(
                        text = "Отмена".uppercase(),
                        style = configuration.headerTextStyle
                    )
                }
                TextButton(onClick = { onDateSelected(selectedDate) }) {
                    Text(
                        text = "Ok".uppercase(),
                        style = configuration.headerTextStyle
                    )
                }
            }
        }
    }
}


@Composable
private fun MonthAndYearView(
    modifier: Modifier = Modifier,
    selectedDate: Calendar,
    onMonthChange: (Int) -> Unit,
    onYearChange: (Int) -> Unit,
    years: List<String>,
    months: List<String>,
    height: Dp,
    configuration: DatePickerConfiguration
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize(),
    ) {
        Box(
            modifier = modifier
                .padding(horizontal = medium)
                .fillMaxWidth()
                .height(configuration.selectedMonthYearAreaHeight)
                .background(
                    color = configuration.selectedMonthYearAreaColor,
                    shape = configuration.selectedMonthYearAreaShape
                )
        )
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SwipeLazyColumn(
                modifier = Modifier.weight(0.5f),
                selectedIndex = selectedDate[Calendar.MONTH],
                onSelectedIndexChange = onMonthChange,
                items = months,
                height = height,
                configuration = configuration
            )
            SwipeLazyColumn(
                modifier = Modifier.weight(0.5f),
                selectedIndex = selectedDate[Calendar.YEAR] - Calendar.getInstance()[Calendar.YEAR] + 20,
                onSelectedIndexChange = onYearChange,
                items = years,
                alignment = Alignment.CenterEnd,
                height = height,
                configuration = configuration
            )
        }
    }
}

@Composable
private fun SwipeLazyColumn(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    items: List<String>,
    alignment: Alignment = Alignment.CenterStart,
    configuration: DatePickerConfiguration,
    height: Dp
) {
    val coroutineScope = rememberCoroutineScope()
    var isAutoScrolling by remember { mutableStateOf(false) }
    val listState = rememberLazyListState(selectedIndex)
    SwipeLazyColumn(
        modifier = modifier,
        selectedIndex = selectedIndex,
        onSelectedIndexChange = onSelectedIndexChange,
        isAutoScrolling = isAutoScrolling,
        height = height,
        numberOfRowsDisplayed = configuration.numberOfMonthYearRowsDisplayed,
        listState = listState,
        onScrollingStopped = {}
    ) {
        val count = items.size + configuration.numberOfMonthYearRowsDisplayed - 1
        items(count) {
            SliderItem(
                value = it,
                selectedIndex = selectedIndex,
                items = items,
                configuration = configuration,
                alignment = alignment,
                height = height,
                onItemClick = { index ->
                    onSelectedIndexChange(index)
                    coroutineScope.launch {
                        isAutoScrolling = true
                        onSelectedIndexChange(index)
                        listState.animateScrollToItem(index)
                        isAutoScrolling = false
                    }
                }
            )
        }
    }
}

@Composable
private fun CalendarHeader(
    modifier: Modifier = Modifier,
    title: String,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onMonthYearClick: () -> Unit,
    configuration: DatePickerConfiguration
) {
    Box(
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, top = 10.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            style = configuration.headerTextStyle,
            modifier = modifier
                .padding(start = medium)
                .noRippleClickable { onMonthYearClick() }
                .align(Alignment.CenterStart),
        )

        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowLeft,
                contentDescription = "leftArrow",
                tint = configuration.headerArrowColor,
                modifier = Modifier
                    .size(configuration.headerArrowSize)
                    .noRippleClickable { onPreviousClick() }
            )
            Spacer(modifier = Modifier.width(medium))
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowRight,
                contentDescription = "rightArrow",
                tint = configuration.headerArrowColor,
                modifier = Modifier
                    .size(configuration.headerArrowSize)
                    .noRippleClickable { onNextClick() }
            )
        }
    }
}

@Composable
private fun DateView(
    modifier: Modifier = Modifier,
    currentDate: Calendar,
    selectedDate: Calendar,
    timeIntervalToPeriodInMillisMapper: TimeIntervalToPeriodInMillisMapper,
    events: List<EventOnPicker>,
    onDaySelected: (Calendar) -> Unit,
    height: Dp,
    configuration: DatePickerConfiguration = DatePickerConfiguration.builder()
) {
    val monthDayCalendarInterator = Calendar.getInstance()
    monthDayCalendarInterator.time = currentDate.time
    monthDayCalendarInterator[Calendar.DAY_OF_MONTH] = 0
    val numberOfDays = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH)
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        userScrollEnabled = false,
        modifier = modifier
    ) {
        items(Constant.days) {
            DateViewHeaderItem(day = it, configuration = configuration)
        }
        val count = numberOfDays + monthDayCalendarInterator[Calendar.DAY_OF_WEEK] - 1
        val topPaddingForItem =
            getTopPaddingForItem(
                count,
                height - configuration.selectedDateBackgroundSize * 2,
                configuration.selectedDateBackgroundSize
            )
        items(count) {
            if (it < monthDayCalendarInterator[Calendar.DAY_OF_WEEK] - 1) return@items
            monthDayCalendarInterator.add(Calendar.DAY_OF_MONTH, 1)
            val eventsCount = events.count {
                it.time in timeIntervalToPeriodInMillisMapper.invoke(
                    TimeInterval.Today,
                    0,
                    monthDayCalendarInterator
                )
            }
            DateViewBodyItem(
                day = monthDayCalendarInterator.copy(),
                isSelected = selectedDate.isEqualByDay(monthDayCalendarInterator),
                isSunday = (it + 1) % 7 == 0 || (it + 1) % 7 == 6,
                eventsCount = eventsCount,
                onDaySelected = onDaySelected,
                topPaddingForItem = topPaddingForItem,
                configuration = configuration,
            )
        }
    }
}

@Composable
private fun DateViewBodyItem(
    day: Calendar,
    isSelected: Boolean,
    isSunday: Boolean,
    eventsCount: Int = 0,
    onDaySelected: (Calendar) -> Unit,
    topPaddingForItem: Dp,
    configuration: DatePickerConfiguration,
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .padding(top = if (day[Calendar.DAY_OF_MONTH] < 7) 0.dp else topPaddingForItem)
                .size(configuration.selectedDateBackgroundSize)
                .clip(configuration.selectedDateBackgroundShape)
                .noRippleClickable { onDaySelected(day) }
                .background(if (isSelected) configuration.selectedDateBackgroundColor else Color.Transparent)
        ) {
            Text(
                text = "${day[Calendar.DAY_OF_MONTH]}",
                textAlign = TextAlign.Center,
                style = if (isSelected) configuration.selectedDateTextStyle
                    .copy(color = configuration.selectedDateTextStyle.color)
                else configuration.dateTextStyle.copy(
                    color = if (isSunday) configuration.sundayTextColor
                    else configuration.dateTextStyle.color
                ),
            )
            if (eventsCount>0) {
                Text(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp),
                    text = "$eventsCount",
                    textAlign = TextAlign.Right,
                    style = if (isSelected) TextStyle(
                        fontSize = 8.sp,
                        fontWeight = FontWeight.W600,
                        color = white
                    )
                    else TextStyle(
                        fontSize = 8.sp,
                        fontWeight = FontWeight.W600,
                        color = black()
                    ),
                )
            }
        }
    }
}

@Composable
private fun DateViewHeaderItem(
    configuration: DatePickerConfiguration,
    day: Days
) {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .size(configuration.selectedDateBackgroundSize)
    ) {
        Text(
            text = day.abbreviation,
            textAlign = TextAlign.Center,
            style = configuration.daysNameTextStyle.copy(
                color = if (day.isRedDay) configuration.sundayTextColor else configuration.daysNameTextStyle.color
            ),
        )
    }
}

private fun getTopPaddingForItem(
    count: Int,
    height: Dp,
    size: Dp
): Dp {
    val numberOfRowsVisible = ceil(count.toDouble() / 7) - 1
    val result =
        (height - (size * numberOfRowsVisible.toInt()) - medium) / numberOfRowsVisible.toInt()
    return if (result > 0.dp) result else 0.dp
}

@Composable
@Preview
fun DateTimePickerPreview() {
    KoinApplication(application = {
        modules(mapperModule)
    }) {
        DateTimePicker(
            onDateSelected = { _ -> },
            events = listOf(
                EventOnPicker(
                    time = 1721811173706,
                    iconName = "quant_lsd"
                ),
                EventOnPicker(
                    time = 1721801073706,
                    iconName = "quant_run"
                ),
                EventOnPicker(
                    time = 1721401073706,
                    iconName = "quant_sleep"
                )
            )
        )
    }
}

private val mapperModule = module {
    single { TimeIntervalToPeriodInMillisMapper(get()) }
    single<IDateTimeRepository> { DateTimeRepository() }
}
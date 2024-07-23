package com.skyfolk.quantoflife.ui.now.date_picker

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skyfolk.quantoflife.ui.now.date_picker.DefaultDatePickerConfig.Companion.height
import com.skyfolk.quantoflife.ui.now.date_picker.Size.medium
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import kotlin.math.ceil

@Composable
fun DateTimePicker(
    onDateSelected: (Calendar) -> Unit,
    startDate: Calendar = Calendar.getInstance(),
    configuration: DatePickerConfiguration = DatePickerConfiguration.builder(),
) {
    var isMonthYearPickerVisible by remember { mutableStateOf(false) }
    var currentDate by remember { mutableStateOf(startDate) }
    var selectedDate by remember { mutableStateOf(startDate) }

    Box {
        CalendarHeader(
            title = "${
                currentDate.getDisplayName(
                    Calendar.MONTH,
                    Calendar.LONG,
                    Locale.getDefault()
                )
            } ${currentDate[Calendar.YEAR]}",
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
                .padding(top = configuration.headerHeight)
                .height(height)
        ) {
            AnimatedFadeVisibility(
                visible = !isMonthYearPickerVisible
            ) {
                DateView(
                    currentDate = currentDate,
                    selectedDate = selectedDate,
                    onDaySelected = {
                        Log.d("skyfolk-picker", "DateTimePicker: $it")
                        val tmp = Calendar.getInstance()
                        tmp.time = it.time
                        selectedDate = tmp
                    },
                    height = 100.dp
                )
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
        // I add some empty rows at the beginning and end of list to make it feel that it is a center focused list
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
fun AnimatedFadeVisibility(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 400, delayMillis = 200)),
        exit = fadeOut(animationSpec = tween(durationMillis = 250, delayMillis = 100))
    ) {
        content()
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
    selectedDate: Calendar?,
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
                height - configuration.selectedDateBackgroundSize * 2, // because I don't want to count first two rows
                configuration.selectedDateBackgroundSize
            )
        repeat(30) {
            monthDayCalendarInterator.add(Calendar.DAY_OF_MONTH, 1)
            Text(
                modifier = Modifier.clickable { onDaySelected(monthDayCalendarInterator) },
                text = "${monthDayCalendarInterator[Calendar.DAY_OF_MONTH]}",
                textAlign = TextAlign.Center,
                style = configuration.selectedDateTextStyle
            )
        }
        items(count) {
            if (it < monthDayCalendarInterator[Calendar.DAY_OF_WEEK] - 1) return@items
            monthDayCalendarInterator.add(Calendar.DAY_OF_MONTH, 1)
            DateViewBodyItem(
                day = monthDayCalendarInterator.copy(),
                isSelected = selectedDate == monthDayCalendarInterator,
                isSunday = (it + 1) % 7 == 0 || (it + 1) % 7 == 6,
                onDaySelected = onDaySelected,
                topPaddingForItem = topPaddingForItem,
                configuration = configuration,
            )
        }
    }
}

fun Calendar.copy(): Calendar {
    val tmp = Calendar.getInstance()
    tmp.time = this.time
    return tmp
}


@Composable
private fun DateViewBodyItem(
    day: Calendar,
    isSelected: Boolean,
    isSunday: Boolean,
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
                .noRippleClickable {
                    onDaySelected(day).also {
                        Log.d("skyfolk-picker", "onClick: ${day[Calendar.DAY_OF_MONTH]}")
                    }
                }
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

// Not every month has same number of weeks, so to maintain the same size for calender I add padding if there are less weeks
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
    DateTimePicker(
        onDateSelected = { _ -> }
    )
}
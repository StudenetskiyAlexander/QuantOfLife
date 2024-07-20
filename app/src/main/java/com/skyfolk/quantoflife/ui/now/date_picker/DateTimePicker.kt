package com.skyfolk.quantoflife.ui.now.date_picker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import kotlin.math.ceil

@Composable
fun DateTimePicker(
    onDateSelected: (Calendar) -> Unit,
    currentDate: Calendar = Calendar.getInstance(),
    configuration: DatePickerConfiguration = DatePickerConfiguration.Builder().build(),
) {
//    vm
    Box {
        // TODO add sliding effect when next or previous arrow is pressed
        CalendarHeader(
            title = "July 2024",
            onMonthYearClick = { },
            onNextClick = { },
            onPreviousClick = { },
            isPreviousNextVisible = true,
            themeColor = configuration.selectedDateBackgroundColor,
            configuration = configuration,
        )
        Box(
            modifier = Modifier
                .padding(top = configuration.headerHeight)
                .height(height)
        ) {
            AnimatedFadeVisibility(
                visible = true
            ) {
                DateView(
                    selectedYear = 2024,
                    currentVisibleMonth = Month(
                        name = "July",
                        numberOfDays = 30,
                        firstDayOfMonth = Days.MONDAY,
                        number = 6
                    ),
                    selectedDayOfMonth = 5,
                    onDaySelected = {},
                    selectedMonth = Month(
                        name = "July",
                        numberOfDays = 30,
                        firstDayOfMonth = Days.MONDAY,
                        number = 6
                    ), height = 100.dp
                )
            }
            AnimatedFadeVisibility(
                visible = false
            ) {
                MonthAndYearView(
                    modifier = Modifier.align(Alignment.Center),
                    selectedMonth = 7,
                    onMonthChange = { },
                    selectedYear = 2024,
                    onYearChange = { },
                    years = listOf(),
                    months = listOf(),
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
    selectedMonth: Int,
    onMonthChange: (Int) -> Unit,
    selectedYear: Int,
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
                selectedIndex = selectedMonth,
                onSelectedIndexChange = onMonthChange,
                items = months,
                height = height,
                configuration = configuration
            )
            SwipeLazyColumn(
                modifier = Modifier.weight(0.5f),
                selectedIndex = selectedYear,
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
    isPreviousNextVisible: Boolean,
    configuration: DatePickerConfiguration,
    themeColor: Color
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .height(configuration.headerHeight)
    ) {
        val textColor by
        animateColorAsState(
            targetValue = if (isPreviousNextVisible) configuration.headerTextStyle.color else themeColor,
            animationSpec = tween(durationMillis = 400, delayMillis = 100)
        )
        Text(
            text = title,
            style = configuration.headerTextStyle.copy(color = textColor),
            modifier = modifier
                .padding(start = medium)
                .noRippleClickable { onMonthYearClick() }
                .align(Alignment.CenterStart),
        )

        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
            AnimatedFadeVisibility(visible = isPreviousNextVisible) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowLeft,
                    contentDescription = "leftArrow",
                    tint = configuration.headerArrowColor,
                    modifier = Modifier
                        .size(configuration.headerArrowSize)
                        .noRippleClickable { onPreviousClick() }
                )
            }
            Spacer(modifier = Modifier.width(medium))
            AnimatedFadeVisibility(visible = isPreviousNextVisible) {
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
}

@Composable
private fun DateView(
    modifier: Modifier = Modifier,
    selectedYear: Int,
    currentVisibleMonth: Month,
    selectedDayOfMonth: Int?,
    onDaySelected: (Int) -> Unit,
    selectedMonth: Month,
    height: Dp,
    configuration: DatePickerConfiguration = DatePickerConfiguration.Builder().build()
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        userScrollEnabled = false,
        modifier = modifier
    ) {
        items(Constant.days) {
            DateViewHeaderItem(day = it, configuration = configuration)
        }
        // since I may need few empty cells because every month starts with a different day(Monday, Tuesday, ..)
        // that's way I add some number X into the count
        val count =
            currentVisibleMonth.numberOfDays + currentVisibleMonth.firstDayOfMonth.number - 1
        val topPaddingForItem =
            getTopPaddingForItem(
                count,
                height - configuration.selectedDateBackgroundSize * 2, // because I don't want to count first two rows
                configuration.selectedDateBackgroundSize
            )
        items(count) {
            if (it < currentVisibleMonth.firstDayOfMonth.number - 1) return@items // to create empty boxes
            DateViewBodyItem(
                value = it,
                currentVisibleMonth = currentVisibleMonth,
                selectedYear = selectedYear,
                selectedMonth = selectedMonth,
                selectedDayOfMonth = selectedDayOfMonth,
                onDaySelected = onDaySelected,
                topPaddingForItem = topPaddingForItem,
                configuration = configuration,
            )
        }
    }
}


@Composable
private fun DateViewBodyItem(
    value: Int,
    currentVisibleMonth: Month,
    selectedYear: Int,
    selectedMonth: Month,
    selectedDayOfMonth: Int?,
    onDaySelected: (Int) -> Unit,
    topPaddingForItem: Dp,
    configuration: DatePickerConfiguration,
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        val day = value - currentVisibleMonth.firstDayOfMonth.number + 2
        val isSelected = day == selectedDayOfMonth && selectedMonth == currentVisibleMonth
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .padding(top = if (value < 7) 0.dp else topPaddingForItem) // I don't want first row to have any padding
                .size(configuration.selectedDateBackgroundSize)
                .clip(configuration.selectedDateBackgroundShape)
                .noRippleClickable { onDaySelected(day) }
                .background(if (isSelected) configuration.selectedDateBackgroundColor else Color.Transparent)
        ) {
            Text(
                text = "$day",
                textAlign = TextAlign.Center,
                style = if (isSelected) configuration.selectedDateTextStyle
                    .copy(color = configuration.selectedDateTextStyle.color)
                else configuration.dateTextStyle.copy(
                    color = if (day % 7 == 0 || day % 7 == 6) configuration.sundayTextColor
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
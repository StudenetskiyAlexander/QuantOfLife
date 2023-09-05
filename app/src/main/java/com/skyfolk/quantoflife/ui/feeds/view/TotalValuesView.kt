package com.skyfolk.quantoflife.ui.feeds.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skyfolk.quantoflife.entity.QuantCategory
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.entity.QuantFilterMode
import com.skyfolk.quantoflife.ui.feeds.entity.FeedsFragmentState
import com.skyfolk.quantoflife.ui.feeds.view.preview.EVENT_DISPLAYABLE
import com.skyfolk.quantoflife.ui.theme.QuantOfLifeMainTheme
import com.skyfolk.quantoflife.ui.theme.Typography
import java.util.Locale

@Composable
fun TotalValue(
    modifier: Modifier = Modifier,
    description: String,
    value: Double?,
    valueFormatAfterDot: Int = 1,
    style: TextStyle = LocalTextStyle.current
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Всего ${description.lowercase(Locale.ROOT)} :",
            textAlign = TextAlign.Left,
            style = style
        )
        Text(
            text = if (value != null) String.format("%.${valueFormatAfterDot}f", value) else "",
            textAlign = TextAlign.Right,
            style = style
        )
    }
}

@Composable
fun TotalValues(modifier: Modifier = Modifier, state: FeedsFragmentState) {
    val descriptionsList = getCategoryArrayNames(state)
    val valuesList = when (state) {
        is FeedsFragmentState.EventsListLoading -> arrayOfNulls<Double?>(descriptionsList.size).toList()
        is FeedsFragmentState.LoadingEventsListCompleted -> getCategoryArrayValues(state)
    }
    val subtitle = when (state) {
        is FeedsFragmentState.EventsListLoading -> "...."
        is FeedsFragmentState.LoadingEventsListCompleted -> "Итого за период найдено ${state.listOfEvents.filter { it.isEvent }.size} событий."
    }
    val totalStarFound: Double? = when (state) {
        is FeedsFragmentState.EventsListLoading -> null
        is FeedsFragmentState.LoadingEventsListCompleted -> state.totalStarFound
    }
    val totalFound: Double? = when (state) {
        is FeedsFragmentState.EventsListLoading -> null
        is FeedsFragmentState.LoadingEventsListCompleted -> state.totalFound
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        SmallSubtitle(text = subtitle)
        TotalValue(description = descriptionsList[0], value = valuesList[0])
        TotalValue(description = descriptionsList[1], value = valuesList[1])
        TotalValue(description = descriptionsList[2], value = valuesList[2])
        TotalValue(
            description = "звезд",
            value = totalStarFound,
            valueFormatAfterDot = 1
        )
        TotalValue(
            description = "",
            value = totalFound,
            style = Typography.subtitle2,
            valueFormatAfterDot = 2
        )
        SeparatorLine()
    }
}

@Preview
@Composable
private fun TotalValuesLoadingPreview() {
    QuantOfLifeMainTheme {
        TotalValues(state = FeedsFragmentState.EventsListLoading(
            listOfQuants = listOf(),
            selectedTimeInterval = TimeInterval.All,
            selectedQuantFilterMode = QuantFilterMode.All,
            selectedTextFilter = "",
            quantCategoryNames = listOf()
        ))
    }
}

@Preview
@Composable
private fun TotalValuesCompletedPreview() {
    QuantOfLifeMainTheme {
        TotalValues(
            state = FeedsFragmentState.LoadingEventsListCompleted(
                listOfQuants = listOf(),
                selectedTimeInterval = TimeInterval.All,
                selectedQuantFilterMode = QuantFilterMode.All,
                selectedTextFilter = "",
                quantCategoryNames = listOf(),
                listOfEvents = listOf(EVENT_DISPLAYABLE),
                totalPhysicalFound = 1.0,
                totalEmotionalFound = 2.0,
                totalEvolutionFound = 3.0,
                totalStarFound = 4.0,
                totalFound = 1230.0
            )
        )
    }
}

private fun getCategoryArrayNames(state: FeedsFragmentState): List<String> {
    return listOf(
        state.quantCategoryNames.firstOrNull { it.first == QuantCategory.Physical }?.second
            ?: "",
        state.quantCategoryNames.firstOrNull { it.first == QuantCategory.Emotion }?.second
            ?: "",
        state.quantCategoryNames.firstOrNull { it.first == QuantCategory.Evolution }?.second
            ?: "",
        state.quantCategoryNames.firstOrNull { it.first == QuantCategory.Other }?.second
            ?: ""
    )
}

private fun getCategoryArrayValues(state: FeedsFragmentState.LoadingEventsListCompleted): List<Double> {
    return listOf(
        state.totalPhysicalFound,
        state.totalEmotionalFound,
        state.totalEvolutionFound
    )
}

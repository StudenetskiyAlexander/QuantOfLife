package com.skyfolk.quantoflife.ui.feeds

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.RelocationRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyfolk.quantoflife.R
import com.skyfolk.quantoflife.entity.EventDisplayable
import com.skyfolk.quantoflife.entity.QuantCategory
import com.skyfolk.quantoflife.entity.ValueTypeDisplayable
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.entity.QuantFilterMode
import com.skyfolk.quantoflife.ui.theme.Colors.Orange
import com.skyfolk.quantoflife.ui.theme.Typography
import com.skyfolk.quantoflife.utils.format
import com.skyfolk.quantoflife.utils.timeInMillis
import com.skyfolk.quantoflife.utils.toDate
import com.skyfolk.quantoflife.utils.toDateWithoutHourAndMinutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun TotalValue(
    description: String,
    value: Double?, valueFormatAfterDot: Int = 1,
    style: TextStyle = LocalTextStyle.current
) {
    Row(
        modifier = Modifier
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
fun SmallSubtitle(text: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        text = text,
        style = Typography.subtitle1,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun SeparatorLine() {
    Divider(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .padding(top = 5.dp),
        color = Color.Black,
        thickness = 1.dp
    )
}

@Composable
fun TotalValues(state: FeedsFragmentState, modifier: Modifier) {
    val descriptionsList = getCategoryArrayNames(state)
    val valuesList = when (state) {
        is FeedsFragmentState.EventsListLoading -> arrayOfNulls<Double?>(descriptionsList.size).toList()
        is FeedsFragmentState.LoadingEventsListCompleted -> getCategoryArrayValues(state)
    }
    val subtitle = when (state) {
        is FeedsFragmentState.EventsListLoading -> "...."
        is FeedsFragmentState.LoadingEventsListCompleted -> "Итого за период найдено ${state.listOfEvents.size} событий."
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

@Composable
fun TimeSelectLayout(
    time: Long,
    horizontalArrangement: Arrangement.Horizontal,
    modifier: Modifier = Modifier,
    onTimeSelectClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement
    ) {
        Text(
            text = time.toDateWithoutHourAndMinutes(),
            style = Typography.body2,
        )
        Image(
            painter = painterResource(R.drawable.quant_date),
            contentDescription = "",
            Modifier
                .size(size = 30.dp)
                .clickable(onClick = { onTimeSelectClick() })
        )
    }
}

@Composable
fun <T> DropdownSpinner(
    contentMapper: (T) -> String,
    content: List<T>,
    selectedItem: T,
    onItemSelect: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(content.indexOf(selectedItem)) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 5.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = true }),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = contentMapper.invoke(selectedItem)
            )
            Image(
                painter = painterResource(R.drawable.ic_dropdown),
                contentDescription = "",
                Modifier.size(size = 20.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        ) {
            content.forEachIndexed { index, s ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                    onItemSelect(s)
                }) {
                    Text(text = contentMapper.invoke(s))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun TextSearchField(placeholder: String, initialValue: String, onEnter: (String) -> Unit) {
    var value by rememberSaveable { mutableStateOf(initialValue) }
    val relocationRequestor = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .bringIntoViewRequester(relocationRequestor)
                .onFocusChanged {
                    if ("$it" == "Active") {
                        scope.launch {
                            delay(200)
                            relocationRequestor.bringIntoView()
                        }
                    }
                },
            value = value,
            textStyle = TextStyle(
                color = Color(0xFFFFFFFF)
            ),
            onValueChange = {
                value = it
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onEnter(value)
                }
            ),
            decorationBox = { innerTextField ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = 14.sp,
                            maxLines = 1,
                            color = Color.Gray,
                        )
                    }
                }
                innerTextField()
            }
        )

        Image(
            painter = painterResource(R.drawable.ic_clear_search),
            contentDescription = "",
            Modifier
                .size(size = 20.dp)
                .clickable {
                    value = ""
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onEnter(value)
                }
        )
    }

}

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

@ExperimentalComposeUiApi
@Composable
fun FilterBlock(
    listOfQuantFilterModes: List<QuantFilterMode>,
    selectedQuantFilterMode: QuantFilterMode,
    onSelectQuantFilterMode: (QuantFilterMode) -> Unit,
    listOfTimeInterval: List<TimeInterval>,
    selectedTimeInterval: TimeInterval,
    selectedTextFilter: String,
    onSelectTimeInterval: (TimeInterval) -> Unit,
    onTextSearchEnter: (String) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            SmallSubtitle(text = "Фильтры.")

            TextSearchField(
                initialValue = selectedTextFilter,
                placeholder = "Поиск по словам в названии или примечании",
                onEnter = onTextSearchEnter
            )

            DropdownSpinner(
                contentMapper = QuantFilterToContentMapper(),
                content = listOfQuantFilterModes,
                selectedItem = selectedQuantFilterMode
            ) {
                onSelectQuantFilterMode(it)
            }
            DropdownSpinner(
                contentMapper = TimeIntervalToContentMapper(),
                content = listOfTimeInterval,
                selectedItem = selectedTimeInterval
            ) {
                onSelectTimeInterval(it)
            }
            SeparatorLine()

            if (selectedTimeInterval is TimeInterval.Selected) {
                SelectedTimeInterval(
                    LocalContext.current,
                    Calendar.getInstance().timeInMillis(selectedTimeInterval.start),
                    Calendar.getInstance().timeInMillis(selectedTimeInterval.end)
                ) { onSelectTimeInterval(it) }
            }
        }
    }
}

@Composable
fun EventItem(event: EventDisplayable, onItemClick: (EventDisplayable) -> Unit) {
    Card(
        backgroundColor = Orange,
        modifier = Modifier
            .clickable {
                onItemClick(event)
            }
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Row(modifier = Modifier) {
            var imageResource = LocalContext.current.resources.getIdentifier(
                event.icon,
                "drawable",
                LocalContext.current.packageName
            )
            if (imageResource == 0) {
                imageResource = LocalContext.current.resources.getIdentifier(
                    "quant_default",
                    "drawable",
                    LocalContext.current.packageName
                )
            }
            Image(
                painter = painterResource(imageResource),
                contentDescription = "event_icon",
                modifier = Modifier
                    .height(50.dp)
                    .width(50.dp)
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(event.name)
                    Text(event.date.toDate(), fontSize = 12.sp)
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    when (event.valueType) {
                        ValueTypeDisplayable.STARS -> {
                            event.value ?: return@Row
                            event.bonuses ?: return@Row

                            RatingBar(
                                rating = event.value.toFloat(),
                                color = Color.Cyan,
                                modifier = Modifier.height(20.dp)
                            )

                            var physicalBonus = 0.0
                            var emotionBonus = 0.0
                            var evolutionBonus = 0.0

                            for (bonus in event.bonuses) {
                                when (bonus.category) {
                                    QuantCategory.Physical -> {
                                        physicalBonus += bonus.baseBonus + bonus.bonusForEachRating * event.value
                                    }
                                    QuantCategory.Emotion -> {
                                        emotionBonus += bonus.baseBonus + bonus.bonusForEachRating * event.value
                                    }
                                    QuantCategory.Evolution -> {
                                        evolutionBonus += bonus.baseBonus + bonus.bonusForEachRating * event.value
                                    }
                                    else -> {}
                                }
                            }

                            Text(
                                "${physicalBonus.format(1)}|${emotionBonus.format(1)}|${
                                    evolutionBonus.format(
                                        1
                                    )
                                }", fontSize = 14.sp, fontStyle = FontStyle.Italic, maxLines = 1
                            )
                        }
                        ValueTypeDisplayable.NUMBER -> {
                            // TODO value
                            Text("= ${event.value}")
                        }
                        ValueTypeDisplayable.NOTHING -> {}
                    }
                }

                Text(event.note, fontSize = 12.sp, maxLines = 3)
            }
        }
    }
}

@Preview
@Composable
fun EventItemPreview() {
    val event = EventDisplayable(
        id = "id000",
        name = "Event name",
        quantId = "quantId",
        icon = "quant_cardio",
        date = Calendar.getInstance().timeInMillis,
        note = "Заметка. Много строк - очень-очень длинный текст заметки-примечания для события, где описывается что-то прям важное и нужное.",
        value = 4.0,
        valueType = ValueTypeDisplayable.NUMBER,
        bonuses = arrayListOf()
    )
    EventItem(event = event, onItemClick = {})
}

@Composable
fun EventsList(modifier: Modifier, events: List<EventDisplayable>, onItemClick: (String) -> Unit) {
    LazyColumn(modifier = modifier) {
        events.map {
            item {
                EventItem(
                    event = it,
                    onItemClick = {
                        onItemClick(it.id)
                    })
            }
        }
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
        TimeSelectLayout(
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
        TimeSelectLayout(
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
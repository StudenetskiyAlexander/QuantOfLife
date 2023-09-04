package com.skyfolk.quantoflife.ui.feeds.view

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
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
import com.skyfolk.quantoflife.entity.EventListItem
import com.skyfolk.quantoflife.entity.QuantCategory
import com.skyfolk.quantoflife.entity.ValueTypeDisplayable
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.entity.QuantFilterMode
import com.skyfolk.quantoflife.ui.feeds.entity.FeedsFragmentState
import com.skyfolk.quantoflife.ui.theme.Colors.Orange
import com.skyfolk.quantoflife.ui.theme.Typography
import com.skyfolk.quantoflife.utils.format
import com.skyfolk.quantoflife.utils.timeInMillis
import com.skyfolk.quantoflife.utils.toDate
import com.skyfolk.quantoflife.utils.toDateWithoutHourAndMinutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

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
fun EventSeparatorLine(
    separatorLine: EventListItem.SeparatorLine
) {
    Card(
        backgroundColor = Orange,
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(
            separatorLine.text, modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
fun EventItem(
    event: EventListItem.EventDisplayable,
    onItemClick: (EventListItem.EventDisplayable) -> Unit
) {
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
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .width(50.dp)
            ) {
                Image(
                    painter = painterResource(imageResource),
                    contentDescription = "event_icon",
                    modifier = Modifier
                        .height(50.dp)
                        .width(50.dp)
                )
                if (event.isHidden) {
                    Image(
                        painter = painterResource(R.drawable.quant_hidden),
                        contentDescription = "hidden",
                        modifier = Modifier
                            .padding(start = 5.dp, top = 5.dp)
                            .height(20.dp)
                            .width(20.dp)
                    )
                }
            }
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

                Text(event.note, fontSize = 12.sp, maxLines = 4)
            }
        }
    }
}

@Preview
@Composable
fun EventItemPreview() {
    val event = EventListItem.EventDisplayable(
        id = "id000",
        name = "Event name",
        quantId = "quantId",
        icon = "quant_cardio",
        date = Calendar.getInstance().timeInMillis,
        note = "Заметка. Много строк - очень-очень длинный текст заметки-примечания для события, где описывается что-то прям важное и нужное.",
        value = 4.0,
        valueType = ValueTypeDisplayable.STARS,
        bonuses = arrayListOf(),
        isHidden = true
    )
    EventItem(event = event, onItemClick = {})
}

@Preview
@Composable
fun EventSeparatorLinePreview() {
    EventSeparatorLine(separatorLine = EventListItem.SeparatorLine("new day"))
}

@Composable
fun EventsList(modifier: Modifier, events: List<EventListItem>, onItemClick: (String) -> Unit) {
    LazyColumn(modifier = modifier) {
        events.map {
            item {
                when (it) {
                    is EventListItem.EventDisplayable -> EventItem(
                        event = it,
                        onItemClick = {
                            onItemClick(it.id)
                        })

                    is EventListItem.SeparatorLine -> EventSeparatorLine(it)
                }

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
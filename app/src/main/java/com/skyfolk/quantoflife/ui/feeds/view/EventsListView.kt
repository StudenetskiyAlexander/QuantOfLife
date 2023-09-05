package com.skyfolk.quantoflife.ui.feeds.view

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.skyfolk.quantoflife.entity.EventListItem
import com.skyfolk.quantoflife.ui.feeds.view.preview.EVENTS_SEPARATOR_LINE
import com.skyfolk.quantoflife.ui.feeds.view.preview.EVENT_DISPLAYABLE
import com.skyfolk.quantoflife.ui.theme.QuantOfLifeMainTheme

@Composable
fun EventsList(
    modifier: Modifier = Modifier,
    events: List<EventListItem>,
    onItemClick: (String) -> Unit
) {
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

@Preview
@Composable
private fun EventsListPreview() {
    QuantOfLifeMainTheme {
        EventsList(
            events = listOf(
                EVENTS_SEPARATOR_LINE,
                EVENT_DISPLAYABLE,
                EVENT_DISPLAYABLE,
                EVENTS_SEPARATOR_LINE,
                EVENT_DISPLAYABLE
            ), onItemClick = {})
    }
}
package com.skyfolk.quantoflife.ui.feeds.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skyfolk.quantoflife.entity.EventListItem
import com.skyfolk.quantoflife.entity.QuantBonusBase
import com.skyfolk.quantoflife.entity.QuantCategory
import com.skyfolk.quantoflife.ui.feeds.view.preview.EVENT_DISPLAYABLE
import com.skyfolk.quantoflife.ui.theme.Colors

@Composable
fun EventSeparatorLine(
    separatorLine: EventListItem.SeparatorLine,
) {
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(5.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 5.dp,
                    topEnd = 5.dp,
                    bottomEnd = 5.dp,
                    bottomStart = 5.dp,
                )
            )
            .background(Colors.Orange),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = separatorLine.text,
            textAlign = TextAlign.Center,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(horizontal = 40.dp)
        )
        BonusesTotalView(
            events = separatorLine.events,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
    }
}

@Preview
@Composable
fun EventSeparatorLinePreview() {
    EventSeparatorLine(
        separatorLine = EventListItem.SeparatorLine(
            "new day",
            listOf(
                EVENT_DISPLAYABLE,
                EVENT_DISPLAYABLE.copy(
                    bonuses = arrayListOf(
                        QuantBonusBase.QuantBonusRated(
                            category = QuantCategory.Physical,
                            baseBonus = 2.0,
                            bonusForEachRating = 0.5
                        )
                    ),
                ),
            )
        )
    )
}

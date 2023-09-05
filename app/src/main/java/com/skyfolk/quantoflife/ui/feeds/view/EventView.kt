package com.skyfolk.quantoflife.ui.feeds.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyfolk.quantoflife.R
import com.skyfolk.quantoflife.entity.EventListItem
import com.skyfolk.quantoflife.entity.QuantCategory
import com.skyfolk.quantoflife.entity.ValueTypeDisplayable
import com.skyfolk.quantoflife.ui.feeds.view.preview.EVENT_DISPLAYABLE
import com.skyfolk.quantoflife.ui.theme.Colors
import com.skyfolk.quantoflife.utils.format
import com.skyfolk.quantoflife.utils.toDate
import java.util.Calendar


@Composable
fun EventItem(
    event: EventListItem.EventDisplayable,
    onItemClick: (EventListItem.EventDisplayable) -> Unit
) {
    Card(
        backgroundColor = Colors.Orange,
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
    EventItem(event = EVENT_DISPLAYABLE, onItemClick = {})
}
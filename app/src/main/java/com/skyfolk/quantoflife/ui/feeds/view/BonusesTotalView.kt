package com.skyfolk.quantoflife.ui.feeds.view

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.skyfolk.quantoflife.entity.EventListItem
import com.skyfolk.quantoflife.entity.QuantBonusBase
import com.skyfolk.quantoflife.entity.QuantCategory
import com.skyfolk.quantoflife.ui.feeds.view.preview.EVENT_DISPLAYABLE
import com.skyfolk.quantoflife.utils.format

@Composable
fun BonusesTotalView(
    events: List<EventListItem.EventDisplayable>,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
) {
    var physicalBonus = 0.0
    var emotionBonus = 0.0
    var evolutionBonus = 0.0

    events.forEach { event ->
        event.bonuses ?: return@forEach
        event.value ?: return@forEach

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
    }

    val allBonuses = physicalBonus + emotionBonus + evolutionBonus
    Text(
        "${allBonuses.format(1)} : ${physicalBonus.format(1)}|${emotionBonus.format(1)}|${
            evolutionBonus.format(
                1
            )
        }",
        fontSize = fontSize,
        fontStyle = FontStyle.Italic,
        maxLines = 1,
        modifier = modifier,
        textAlign = TextAlign.Center,
    )
}

@Composable
@Preview
private fun BonusesTotalViewPreview() {
    BonusesTotalView(
        listOf(
            EVENT_DISPLAYABLE,
            EVENT_DISPLAYABLE.copy(
                bonuses = arrayListOf(
                    QuantBonusBase.QuantBonusRated(
                        category = QuantCategory.Physical, baseBonus = 2.0, bonusForEachRating = 0.5
                    )
                ),
            ),
        )
    )
}
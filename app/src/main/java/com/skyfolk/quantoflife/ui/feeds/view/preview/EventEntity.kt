package com.skyfolk.quantoflife.ui.feeds.view.preview

import com.skyfolk.quantoflife.entity.EventListItem
import com.skyfolk.quantoflife.entity.QuantBonusBase
import com.skyfolk.quantoflife.entity.QuantCategory
import com.skyfolk.quantoflife.entity.ValueTypeDisplayable
import java.util.Calendar

val EVENT_DISPLAYABLE = EventListItem.EventDisplayable(
    id = "id000",
    name = "Event name",
    quantId = "quantId",
    icon = "quant_cardio",
    date = Calendar.getInstance().timeInMillis,
    note = "Заметка. Много строк - очень-очень длинный текст заметки-примечания для события, где описывается что-то прям важное и нужное.",
    value = 4.0,
    valueType = ValueTypeDisplayable.STARS,
    bonuses = arrayListOf(QuantBonusBase.QuantBonusRated(
        category = QuantCategory.Emotion, baseBonus = 1.0, bonusForEachRating = 0.5
    )),
    isHidden = true
)

val EVENTS_SEPARATOR_LINE = EventListItem.SeparatorLine(
    text = "4 сентября 2023"
)
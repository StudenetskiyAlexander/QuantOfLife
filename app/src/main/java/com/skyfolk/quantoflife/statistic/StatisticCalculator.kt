package com.skyfolk.quantoflife.statistic

import com.skyfolk.quantoflife.db.IQuantsStorageInteractor
import com.skyfolk.quantoflife.entity.EventBase
import com.skyfolk.quantoflife.entity.QuantBase
import com.skyfolk.quantoflife.entity.QuantCategory

fun getTotal(quantsStorageInteractor: IQuantsStorageInteractor, events: List<EventBase>, category: QuantCategory = QuantCategory.None) : Double {
    var total = 0.0

    for (event in events) {
        if (event is EventBase.EventRated) {
            val foundQuant = quantsStorageInteractor.getQuantById(event.quantId)
            if (foundQuant is QuantBase.QuantRated) {
                for (bonus in foundQuant.bonuses) {
                    if (bonus.category == category || category == QuantCategory.None) {
                        total += bonus.baseBonus + bonus.bonusForEachRating * event.rate
                    }
                }
            }
        }
    }
    return total
}


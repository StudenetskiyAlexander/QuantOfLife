package com.skyfolk.quantoflife.entity

import java.util.*

sealed class QuantBase(
    @Transient
    open var id: String = UUID.randomUUID().toString(), //TODO Replace to GUID
    @Transient
    open var name: String,
    @Transient
    open var icon: String,
    @Transient
    open var primalCategory: QuantCategory,
    @Transient
    open var description: String,
    @Transient
    open var usageCount: Int = 0,
    @Transient
    open var isHidden: Boolean = false
) {
    data class QuantNote(
        override var id: String = UUID.randomUUID().toString(),
        override var name: String,
        override var icon: String,
        override var primalCategory: QuantCategory,
        override var description: String,
        override var usageCount: Int = 0
    ) : QuantBase(id, name, icon, primalCategory, description, usageCount) {

        override fun copy(): QuantNote {
            return QuantNote(id, name, icon, primalCategory, description, usageCount)
        }
    }

    data class QuantRated(
        override var id: String = UUID.randomUUID().toString(),
        override var name: String,
        override var icon: String,
        override var primalCategory: QuantCategory,
        var bonuses: ArrayList<QuantBonusBase.QuantBonusRated>,
        override var description: String,
        override var usageCount: Int = 0
    ) : QuantBase(id, name, icon, primalCategory, description, usageCount) {

        fun getBonusFor(category: QuantCategory): QuantBonusBase.QuantBonusRated? {
            for (bonus in bonuses) {
                if (bonus.category == category) return bonus
            }
            return null
        }

        override fun copy(): QuantRated {
            val oldBonuses = ArrayList<QuantBonusBase.QuantBonusRated>()
            for (bonus in bonuses) {
                oldBonuses.add(bonus.copy())
            }
            return QuantRated(id, name, icon, primalCategory, oldBonuses, description, usageCount)
        }
    }

    data class QuantMeasure(
        override var id: String = UUID.randomUUID().toString(),
        override var name: String,
        override var icon: String,
        override var primalCategory: QuantCategory,
        override var description: String,
        override var usageCount: Int = 0,
        var minSize: Int,
        var maxSize: Int
    ) : QuantBase(id, name, icon, primalCategory, description, usageCount) {

        override fun copy(): QuantMeasure {
            return QuantMeasure(
                id,
                name,
                icon,
                primalCategory,
                description,
                usageCount,
                minSize,
                maxSize
            )
        }
    }

    fun toEvent(eventId: String? = null, rate: Double, date: Long, note: String, isHidden: Boolean): EventBase {
        return when (this) {
            is QuantRated -> {
                EventBase.EventRated(
                    eventId ?: UUID.randomUUID().toString(),
                    this.id,
                    date,
                    note,
                    rate
                )
            }
            is QuantMeasure -> {
                EventBase.EventMeasure(
                    eventId ?: UUID.randomUUID().toString(),
                    this.id,
                    date,
                    note,
                    rate
                )
            }
            is QuantNote -> {
                EventBase.EventNote(
                    eventId ?: UUID.randomUUID().toString(),
                    this.id,
                    date,
                    note
                )
            }
        }.apply {
            this.isHidden = isHidden
        }
    }

    fun isEqual(quant: QuantBase): Boolean {
        return id == quant.id
    }

    abstract fun copy(): QuantBase
}

sealed class QuantBonusBase(
    @Transient
    open var category: QuantCategory
) {
    data class QuantBonusRated(
        override var category: QuantCategory,
        var baseBonus: Double,
        var bonusForEachRating: Double
    ) : QuantBonusBase(category)
}

enum class QuantCategory {
    Physical, Emotion, Evolution, Other, None, All
}

fun String.toQuantCategory(): QuantCategory {
    return when (this) {
        QuantCategory.All.name -> QuantCategory.All
        QuantCategory.Physical.name -> QuantCategory.Physical
        QuantCategory.Emotion.name -> QuantCategory.Emotion
        QuantCategory.Evolution.name -> QuantCategory.Evolution
        QuantCategory.Other.name -> QuantCategory.Other
        QuantCategory.None.name -> QuantCategory.None
        else -> QuantCategory.None
    }
}
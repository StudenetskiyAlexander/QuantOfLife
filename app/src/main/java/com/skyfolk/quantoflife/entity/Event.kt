package com.skyfolk.quantoflife.entity

sealed class EventListItem(open val isEvent: Boolean = true) {

    data class EventDisplayable(
        val id: String,
        val name: String,
        val quantId: String,
        val icon: String,
        val date: Long,
        val note: String,
        val value: Double?,
        val valueType: ValueTypeDisplayable,
        val bonuses: ArrayList<QuantBonusBase.QuantBonusRated>?,
        val isHidden: Boolean
    ): EventListItem()

    data class SeparatorLine(
        val text: String
    ): EventListItem(isEvent = false)
}



enum class ValueTypeDisplayable {
    STARS, NUMBER, NOTHING
}

sealed class EventBase(
    open val id: String,
    open var quantId: String,
    open var date: Long,
    open var note: String,
    open var isHidden: Boolean = false
) {
    data class EventNote(
        override val id: String,
        override var quantId: String,
        override var date: Long,
        override var note: String
    ) : EventBase(id, quantId, date, note) {
        override fun copy(): EventNote {
            return EventNote(id, quantId, date, note)
        }
    }

    data class EventMeasure(
        override val id: String,
        override var quantId: String,
        override var date: Long,
        override var note: String,
        var value: Double
    ) : EventBase(id, quantId, date, note) {
        override fun copy(): EventMeasure {
            return EventMeasure(id, quantId, date, note, value)
        }
    }

    data class EventRated(
        override val id: String,
        override var quantId: String,
        override var date: Long,
        override var note: String,
        var rate: Double
    ) : EventBase(id, quantId, date, note) {
        override fun copy(): EventRated {
            return EventRated(id, quantId, date, note, rate)
        }
    }

    fun isEqual(event: EventBase): Boolean {
        return id == event.id
    }

    abstract fun copy(): EventBase
}

fun EventBase.toDisplayableEvents(allQuants: List<QuantBase>): EventListItem.EventDisplayable? {

    allQuants.firstOrNull { it.id == this.quantId }?.let {
        val value = when {
            (this is EventBase.EventRated) -> this.rate
            (this is EventBase.EventMeasure) -> this.value
            else -> null
        }

        val bonuses = if (it is QuantBase.QuantRated) it.bonuses else null
        return EventListItem.EventDisplayable(
            id = this.id,
            name = it.name,
            quantId = this.quantId,
            icon = it.icon,
            date = this.date,
            note = this.note,
            value = value,
            valueType = when (this) {
                is EventBase.EventMeasure -> ValueTypeDisplayable.NUMBER
                is EventBase.EventNote -> ValueTypeDisplayable.NOTHING
                is EventBase.EventRated -> ValueTypeDisplayable.STARS
            },
            bonuses = bonuses,
            isHidden = isHidden
        )
    }
    return null
}



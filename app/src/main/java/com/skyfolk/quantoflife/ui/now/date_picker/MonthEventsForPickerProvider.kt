package com.skyfolk.quantoflife.ui.now.date_picker

import EventOnPicker
import com.skyfolk.quantoflife.db.EventsStorageInteractor
import com.skyfolk.quantoflife.db.IQuantsStorageInteractor
import com.skyfolk.quantoflife.entity.EventBase
import java.util.Calendar

interface MonthEventsForPickerProvider {
    fun provide(currentMonth: Calendar): List<EventOnPicker>
}

class MonthEventsForPickerProviderImpl(
    private val eventsStorageInteractor: EventsStorageInteractor,
    private val quantsStorageInteractor: IQuantsStorageInteractor,
): MonthEventsForPickerProvider {

    override fun provide(currentMonth: Calendar): List<EventOnPicker> {
        val events = eventsStorageInteractor.getAllEventsByMonth(currentMonth)
        val quants = quantsStorageInteractor.getAllQuantsList(true)

        return events.mapNotNull { event ->
            val quant = quants.firstOrNull { it.id == event.quantId }
            quant?.let {
                EventOnPicker(
                    time = event.date,
                    comment = getEventNote(event),
                    iconName = it.icon
                )
            }
        }
    }

    private fun getEventNote(event: EventBase): String = when (event) {
        is EventBase.EventMeasure -> "${event.value}, ${event.note} "
        is EventBase.EventNote -> event.note
        is EventBase.EventRated -> "${event.rate}★, ${event.note}"
    }
}
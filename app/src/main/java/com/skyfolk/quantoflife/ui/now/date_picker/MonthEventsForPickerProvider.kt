package com.skyfolk.quantoflife.ui.now.date_picker

import EventOnPicker
import com.skyfolk.quantoflife.db.EventsStorageInteractor
import com.skyfolk.quantoflife.db.IQuantsStorageInteractor
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
                    iconName = it.icon
                )
            }
        }
    }
}
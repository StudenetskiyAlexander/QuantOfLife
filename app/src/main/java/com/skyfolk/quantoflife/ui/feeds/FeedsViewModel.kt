package com.skyfolk.quantoflife.ui.feeds

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyfolk.quantoflife.IDateTimeRepository
import com.skyfolk.quantoflife.db.EventsStorageInteractor
import com.skyfolk.quantoflife.db.IQuantsStorageInteractor
import com.skyfolk.quantoflife.entity.EventBase
import com.skyfolk.quantoflife.entity.EventDisplayable
import com.skyfolk.quantoflife.entity.QuantBase
import com.skyfolk.quantoflife.entity.QuantCategory
import com.skyfolk.quantoflife.feeds.getStarTotal
import com.skyfolk.quantoflife.feeds.getTotal
import com.skyfolk.quantoflife.settings.SettingsInteractor
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.entity.QuantFilterMode
import com.skyfolk.quantoflife.ui.feeds.FeedsFragmentState.EventsListLoading.Companion.updateStateToLoading
import com.skyfolk.quantoflife.ui.feeds.FeedsFragmentState.LoadingEventsListCompleted.Companion.updateStateToCompleted
import com.skyfolk.quantoflife.utils.SingleLiveEvent
import com.skyfolk.quantoflife.utils.getEndDateCalendar
import com.skyfolk.quantoflife.utils.getStartDateCalendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class FeedsViewModel(
    private val eventsStorageInteractor: EventsStorageInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val quantsStorageInteractor: IQuantsStorageInteractor,
    private val dateTimeRepository: IDateTimeRepository
) : ViewModel() {
    private val _state = MutableStateFlow<FeedsFragmentState>(
        FeedsFragmentState.EventsListLoading(
            listOfQuants = quantsStorageInteractor.getAllQuantsList(false),
            selectedTimeInterval = settingsInteractor.statisticTimeIntervalSelectedElement,
            selectedQuantFilterMode = settingsInteractor.feedsQuantFilterMode,
            selectedTextFilter = settingsInteractor.statisticSearchText,
            quantCategoryNames = settingsInteractor.getCategoryNames()
        )
    )
    val state: StateFlow<FeedsFragmentState> = _state.asStateFlow()

    private val _singleLifeEvent = SingleLiveEvent<FeedsFragmentSingleLifeEvent>()
    val singleLifeEvent: LiveData<FeedsFragmentSingleLifeEvent> get() = _singleLifeEvent

    private fun runSearch() {
        Log.d("skyfolk-timer", "runSearchStart: ${System.currentTimeMillis()}")

        viewModelScope.launch {
            val selectedTimeInterval = _state.value.selectedTimeInterval
            val selectedEventFilter = _state.value.selectedQuantFilterMode

            updateStateToLoading(_state)

            val searchText = settingsInteractor.statisticSearchText

            val startDate =
                dateTimeRepository.getCalendar().getStartDateCalendar(
                    selectedTimeInterval,
                    settingsInteractor.startDayTime
                ).timeInMillis
            val endDate =
                dateTimeRepository.getCalendar().getEndDateCalendar(
                    selectedTimeInterval,
                    settingsInteractor.startDayTime
                ).timeInMillis

            var listOfEvents = eventsStorageInteractor.getAllEvents()
                .filter { it.date in startDate until endDate }
                .filter {
                    it.note.contains(searchText, ignoreCase = true)
                }

            selectedEventFilter.let { filter ->
                listOfEvents = when (filter) {
                    QuantFilterMode.All -> listOfEvents
                    is QuantFilterMode.OnlySelected -> listOfEvents.filter { it.quantId == filter.quant.id }
                }
            }

            Log.d("skyfolk-timer", "runSearchEnd: ${System.currentTimeMillis()}")

            val allQuantsFound = quantsStorageInteractor.getAllQuantsList(false)

            val totalPhysicalFound = getTotal(
                allQuantsFound,
                listOfEvents,
                QuantCategory.Physical
            )

            val totalEmotionalFound = getTotal(
                allQuantsFound,
                listOfEvents,
                QuantCategory.Emotion
            )

            val totalEvolutionFound = getTotal(
                allQuantsFound,
                listOfEvents,
                QuantCategory.Evolution
            )

            val totalFound = getTotal(allQuantsFound, listOfEvents)
            val starFound = getStarTotal(allQuantsFound, listOfEvents)

            updateStateToCompleted(
                _state,
                _timeInterval = selectedTimeInterval,
                _selectedEventFilter = selectedEventFilter,
                _selectedTextFilter = searchText,
                _quantCategoryName = settingsInteractor.getCategoryNames(),
                _listOfEvents = listOfEvents.mapNotNull { it.toDisplayableEvents(allQuantsFound) },
                _totalPhysicalFound = totalPhysicalFound,
                _totalEmotionalFound = totalEmotionalFound,
                _totalEvolutionFound = totalEvolutionFound,
                _totalFound = totalFound,
                _totalStarFound = starFound
            )

            Log.d("skyfolk-timer", "runSearchUpdateStateEnd: ${System.currentTimeMillis()}")
        }
    }

    fun getDefaultCalendar(): Calendar {
        return dateTimeRepository.getCalendar()
    }

    fun setTimeIntervalState(timeInterval: TimeInterval) {
        settingsInteractor.statisticTimeIntervalSelectedElement = timeInterval
        _state.value.selectedTimeInterval = timeInterval
        runSearch()
    }

    fun setSearchText(searchText: String) {
        settingsInteractor.statisticSearchText = searchText
        runSearch()
    }

    fun setSelectedQuantFilterMode(mode: QuantFilterMode) {
        settingsInteractor.feedsQuantFilterMode = mode
        _state.value.selectedQuantFilterMode = mode
        runSearch()
    }

    fun editEvent(eventId: String) {
        viewModelScope.launch {
            eventsStorageInteractor.getAllEvents().firstOrNull { it.id == eventId }?.let { event ->
                quantsStorageInteractor.getQuantById(event.quantId)?.let { quant ->
                    _singleLifeEvent.value =
                        FeedsFragmentSingleLifeEvent.ShowEditEventDialog(quant, event)
                }
            }
        }
    }

    fun eventEdited(event: EventBase) {
        eventsStorageInteractor.addEventToDB(event) { runSearch() }
    }

    fun deleteEvent(event: EventBase) {
        eventsStorageInteractor.deleteEvent(event) { runSearch() }
    }

    private fun getQuantNameById(id: String?): String? {
        id?.let {
            return quantsStorageInteractor.getQuantById(it)?.name
        }
        return null
    }

    private fun getQuantIdByName(name: String?): String? {
        name?.let {
            return quantsStorageInteractor.getQuantIdByName(it)
        }
        return null
    }

    private data class TimeIntervalWasChanged(val timeInterval: TimeInterval)
    private data class EventFilterWasChanged(val eventFilter: String?)
}

fun EventBase.toDisplayableEvents(quants: List<QuantBase>): EventDisplayable? {

    quants.firstOrNull { it.id == this.quantId }?.let {
        val value = when {
            (this is EventBase.EventRated) -> this.rate
            (this is EventBase.EventMeasure) -> this.value
            else -> null
        }

        val bonuses = if (it is QuantBase.QuantRated) it.bonuses else null
        return EventDisplayable(
            id = this.id,
            name = it.name,
            quantId = this.quantId,
            icon = it.icon,
            date = this.date,
            note = this.note,
            value = value,
            bonuses = bonuses
        )
    }
    return null
}




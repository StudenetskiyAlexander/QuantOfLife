package com.skyfolk.quantoflife.ui.feeds

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyfolk.quantoflife.db.EventsStorageInteractor
import com.skyfolk.quantoflife.db.IQuantsStorageInteractor
import com.skyfolk.quantoflife.entity.*
import com.skyfolk.quantoflife.feeds.getStarTotal
import com.skyfolk.quantoflife.feeds.getTotal
import com.skyfolk.quantoflife.mapper.TimeIntervalToPeriodInMillisMapper
import com.skyfolk.quantoflife.settings.SettingsInteractor
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.entity.QuantFilterMode
import com.skyfolk.quantoflife.ui.feeds.FeedsFragmentState.EventsListLoading.Companion.updateStateToLoading
import com.skyfolk.quantoflife.ui.feeds.FeedsFragmentState.LoadingEventsListCompleted.Companion.updateStateToCompleted
import com.skyfolk.quantoflife.utils.SingleLiveEvent
import com.skyfolk.quantoflife.utils.getEndDateCalendar
import com.skyfolk.quantoflife.utils.getStartDateCalendar
import com.skyfolk.quantoflife.utils.toCalendar
import com.skyfolk.quantoflife.utils.toDate
import com.skyfolk.quantoflife.utils.toDateWithoutHourAndMinutes
import com.skyfolk.quantoflife.utils.toShortDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.util.Calendar

class FeedsViewModel(
    private val eventsStorageInteractor: EventsStorageInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val quantsStorageInteractor: IQuantsStorageInteractor,
    private val timeIntervalToPeriodInMillisMapper: TimeIntervalToPeriodInMillisMapper
) : ViewModel() {
    private val _state = MutableStateFlow<FeedsFragmentState>(
        FeedsFragmentState.EventsListLoading(
            listOfQuants = quantsStorageInteractor.getAllQuantsList(false),
            selectedTimeInterval = settingsInteractor.feedsTimeIntervalMode,
            selectedQuantFilterMode = settingsInteractor.feedsQuantFilterMode,
            selectedTextFilter = settingsInteractor.feedsSearchText,
            quantCategoryNames = settingsInteractor.getCategoryNames()
        )
    )
    val state: StateFlow<FeedsFragmentState> = _state.asStateFlow()

    private val _singleLifeEvent = SingleLiveEvent<FeedsFragmentSingleLifeEvent>()
    val singleLifeEvent: LiveData<FeedsFragmentSingleLifeEvent> get() = _singleLifeEvent

    fun runSearch() {
        Log.d("skyfolk-timer", "runSearchStart: ${System.currentTimeMillis()}")

        viewModelScope.launch {
            updateStateToLoading(_state)

            val selectedTimeInterval = _state.value.selectedTimeInterval
            val selectedEventFilter = _state.value.selectedQuantFilterMode
            val searchText = _state.value.selectedTextFilter
            val interval = timeIntervalToPeriodInMillisMapper.invoke(
                selectedTimeInterval,
                settingsInteractor.startDayTime
            )

            val listOfEvents = eventsStorageInteractor.getAllEvents(settingsInteractor.showHidden)
                .filter { it.date in interval }
                .filter {
                    it.note.contains(searchText, ignoreCase = true)
                }
                .filter {
                    when (selectedEventFilter) {
                        is QuantFilterMode.All -> true
                        is QuantFilterMode.OnlySelected -> it.quantId == selectedEventFilter.quant.id
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
                _listOfEvents = addSeparatorLine(listOfEvents, allQuantsFound),
                _totalPhysicalFound = totalPhysicalFound,
                _totalEmotionalFound = totalEmotionalFound,
                _totalEvolutionFound = totalEvolutionFound,
                _totalFound = totalFound,
                _totalStarFound = starFound
            )

            Log.d("skyfolk-timer", "runSearchUpdateStateEnd: ${System.currentTimeMillis()}")
        }
    }

    fun setTimeIntervalState(timeInterval: TimeInterval) {
        settingsInteractor.feedsTimeIntervalMode = timeInterval
        if (timeInterval is TimeInterval.Selected) {
            settingsInteractor.feedsTimeIntervalSelectedStart = timeInterval.start
            settingsInteractor.feedsTimeIntervalSelectedEnd = timeInterval.end
        }
        _state.value.selectedTimeInterval = timeInterval
        runSearch()
    }

    fun setSearchText(searchText: String) {
        settingsInteractor.feedsSearchText = searchText
        _state.value.selectedTextFilter = searchText
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

    fun getStoredSelectedTimeInterval(): LongRange {
        return LongRange(
            settingsInteractor.feedsTimeIntervalSelectedStart,
            settingsInteractor.feedsTimeIntervalSelectedEnd
        )
    }

    private fun addSeparatorLine(
        events: List<EventBase>,
        allQuantsFound: List<QuantBase>
    ): List<EventListItem> {
        val result = mutableListOf<EventListItem>()
        var lastEventCalendar: Calendar? = null
        events.forEach {
            val newDayCalendar = it.date.toCalendar().getStartDateCalendar(
                TimeInterval.Today,
                settingsInteractor.startDayTime
            )
            if (lastEventCalendar != null && newDayCalendar[Calendar.DAY_OF_YEAR] != lastEventCalendar!![Calendar.DAY_OF_YEAR]) {
                result.add(
                    EventListItem.SeparatorLine(
                        lastEventCalendar!!.timeInMillis.toDateWithoutHourAndMinutes()
                    )
                )
            }
            lastEventCalendar = newDayCalendar
            it.toDisplayableEvents(allQuantsFound)?.let { event ->
                result.add(event)
            }
        }
        val firstDate = lastEventCalendar?.getStartDateCalendar(
            TimeInterval.Today,
            settingsInteractor.startDayTime
        )
        firstDate?.timeInMillis?.toDateWithoutHourAndMinutes()?.let {
            result.add(EventListItem.SeparatorLine(it))
        }

        return result
    }
}




package com.skyfolk.quantoflife.ui.feeds.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.skyfolk.quantoflife.db.EventsStorageInteractor
import com.skyfolk.quantoflife.db.IQuantsStorageInteractor
import com.skyfolk.quantoflife.entity.*
import com.skyfolk.quantoflife.feeds.getStarTotal
import com.skyfolk.quantoflife.feeds.getTotal
import com.skyfolk.quantoflife.mapper.TimeIntervalToPeriodInMillisMapper
import com.skyfolk.quantoflife.settings.SettingsInteractor
import com.skyfolk.quantoflife.shared.presentation.vm.BaseViewModel
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.entity.QuantFilterMode
import com.skyfolk.quantoflife.ui.feeds.entity.FeedsFragmentActions
import com.skyfolk.quantoflife.ui.feeds.entity.FeedsFragmentActions.*
import com.skyfolk.quantoflife.ui.feeds.entity.FeedsFragmentSingleLifeEvent
import com.skyfolk.quantoflife.ui.feeds.entity.FeedsFragmentState
import com.skyfolk.quantoflife.ui.feeds.entity.FeedsFragmentState.EventsListLoading.Companion.updateStateToLoading
import com.skyfolk.quantoflife.ui.feeds.entity.FeedsFragmentState.LoadingEventsListCompleted.Companion.updateStateToCompleted
import com.skyfolk.quantoflife.utils.SingleLiveEvent
import com.skyfolk.quantoflife.utils.getStartDateCalendar
import com.skyfolk.quantoflife.utils.toCalendar
import com.skyfolk.quantoflife.utils.toDateWithoutHourAndMinutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class FeedsViewModel(
    private val eventsStorageInteractor: EventsStorageInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val quantsStorageInteractor: IQuantsStorageInteractor,
    private val timeIntervalToPeriodInMillisMapper: TimeIntervalToPeriodInMillisMapper
) : BaseViewModel<FeedsFragmentActions, FeedsFragmentState, FeedsFragmentSingleLifeEvent>() {

    private val _state = MutableStateFlow<FeedsFragmentState>(
        FeedsFragmentState.EventsListLoading(
            listOfQuants = quantsStorageInteractor.getAllQuantsList(
                includeDeleted = false,
                includeHidden = false
            ),
            selectedTimeInterval = settingsInteractor.feedsTimeIntervalMode,
            selectedQuantFilterMode = settingsInteractor.feedsQuantFilterMode,
            selectedTextFilter = settingsInteractor.feedsSearchText,
            quantCategoryNames = settingsInteractor.getCategoryNames()
        )
    )
    override val state: StateFlow<FeedsFragmentState> = _state.asStateFlow()

    private val _singleLifeEvent = SingleLiveEvent<FeedsFragmentSingleLifeEvent>()
    override val singleLifeEvent: LiveData<FeedsFragmentSingleLifeEvent> get() = _singleLifeEvent

    override fun proceedAction(action: FeedsFragmentActions) {
        when (action) {
            is EditEventAction -> editEvent(action)
            is SetSelectedQuantFilterModeAction -> setSelectedQuantFilterMode(action)
            is SetTimeIntervalStateAction -> setTimeIntervalState(action)
            RunSearchAction -> runSearch()
            is SetSearchTextAction -> setSearchText(action)
            is DeleteEventAction -> deleteEvent(action)
            is EventEditedAction -> eventEdited(action)
        }
    }

    fun getStoredSelectedTimeInterval(): LongRange {
        return LongRange(
            settingsInteractor.feedsTimeIntervalSelectedStart,
            settingsInteractor.feedsTimeIntervalSelectedEnd
        )
    }

    private fun runSearch() = viewModelScope.launch {
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

        val allQuantsFound = quantsStorageInteractor.getAllQuantsList(
            includeDeleted = false,
            includeHidden = false
        )

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
    }

    private fun setTimeIntervalState(action: SetTimeIntervalStateAction) {
        settingsInteractor.feedsTimeIntervalMode = action.timeInterval
        if (action.timeInterval is TimeInterval.Selected) {
            settingsInteractor.feedsTimeIntervalSelectedStart = action.timeInterval.start
            settingsInteractor.feedsTimeIntervalSelectedEnd = action.timeInterval.end
        }
        _state.value.selectedTimeInterval = action.timeInterval
        runSearch()
    }

    private fun setSearchText(action: SetSearchTextAction) {
        settingsInteractor.feedsSearchText = action.searchText
        _state.value.selectedTextFilter = action.searchText
        runSearch()
    }

    private fun setSelectedQuantFilterMode(action: SetSelectedQuantFilterModeAction) {
        settingsInteractor.feedsQuantFilterMode = action.mode
        _state.value.selectedQuantFilterMode = action.mode
        runSearch()
    }

    private fun editEvent(action: EditEventAction) {
        viewModelScope.launch {
            eventsStorageInteractor.getAllEvents().firstOrNull { it.id == action.eventId }
                ?.let { event ->
                    quantsStorageInteractor.getQuantById(event.quantId)?.let { quant ->
                        _singleLifeEvent.value =
                            FeedsFragmentSingleLifeEvent.ShowEditEventDialog(quant, event)
                    }
                }
        }
    }

    private fun eventEdited(action: EventEditedAction) {
        eventsStorageInteractor.addEventToDB(action.event) { runSearch() }
    }

    private fun deleteEvent(action: DeleteEventAction) {
        eventsStorageInteractor.deleteEvent(action.event) { runSearch() }
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




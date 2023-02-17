package com.skyfolk.quantoflife.ui.feeds

import com.skyfolk.quantoflife.entity.EventDisplayable
import com.skyfolk.quantoflife.entity.QuantBase
import com.skyfolk.quantoflife.entity.QuantCategory
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.entity.QuantFilterMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed class FeedsFragmentState(
    open val listOfQuants: List<QuantBase>,
    open val selectedTimeInterval: TimeInterval,
    open val selectedEventFilter: QuantFilterMode,
    open val selectedTextFilter: String,
    open val quantCategoryNames: List<Pair<QuantCategory, String>>
) {
    data class EventsListLoading(
        override val listOfQuants: List<QuantBase>,
        override val selectedTimeInterval: TimeInterval,
        override val selectedEventFilter: QuantFilterMode,
        override val selectedTextFilter: String,
        override val quantCategoryNames: List<Pair<QuantCategory, String>>
    ) : FeedsFragmentState(
        listOfQuants,
        selectedTimeInterval,
        selectedEventFilter,
        selectedTextFilter,
        quantCategoryNames
    ) {
        companion object {
            fun updateStateToLoading(state: MutableStateFlow<FeedsFragmentState>) {
                state.update {
                    EventsListLoading(
                        it.listOfQuants,
                        it.selectedTimeInterval,
                        it.selectedEventFilter,
                        it.selectedTextFilter,
                        it.quantCategoryNames
                    )
                }
            }
        }
    }

    data class LoadingEventsListCompleted(
        override val listOfQuants: List<QuantBase>,
        override val selectedTimeInterval: TimeInterval,
        override val selectedEventFilter: QuantFilterMode,
        override val selectedTextFilter: String,
        override val quantCategoryNames: List<Pair<QuantCategory, String>>,
        val listOfEvents: List<EventDisplayable>,
        val totalPhysicalFound: Double,
        val totalEmotionalFound: Double,
        val totalEvolutionFound: Double,
        val totalStarFound: Double,
        val totalFound: Double
    ) : FeedsFragmentState(
        listOfQuants,
        selectedTimeInterval,
        selectedEventFilter,
        selectedTextFilter,
        quantCategoryNames
    ) {
        companion object {
            fun updateStateToCompleted(
                state: MutableStateFlow<FeedsFragmentState>,
                _timeInterval: TimeInterval,
                _selectedEventFilter: QuantFilterMode,
                _selectedTextFilter: String,
                _quantCategoryName: ArrayList<Pair<QuantCategory, String>>,
                _listOfEvents: List<EventDisplayable>,
                _totalPhysicalFound: Double,
                _totalEmotionalFound: Double,
                _totalEvolutionFound: Double,
                _totalStarFound: Double,
                _totalFound: Double
            ) {
                state.update{
                    LoadingEventsListCompleted(
                        it.listOfQuants,
                        _timeInterval,
                        _selectedEventFilter,
                        _selectedTextFilter,
                        _quantCategoryName,
                        _listOfEvents,
                        _totalPhysicalFound,
                        _totalEmotionalFound,
                        _totalEvolutionFound,
                        _totalStarFound,
                        _totalFound
                    )
                }
            }
        }
    }
}

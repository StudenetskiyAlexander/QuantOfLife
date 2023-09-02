package com.skyfolk.quantoflife.ui.feeds.entity

import com.skyfolk.quantoflife.entity.EventListItem
import com.skyfolk.quantoflife.entity.QuantBase
import com.skyfolk.quantoflife.entity.QuantCategory
import com.skyfolk.quantoflife.shared.presentation.state.ScreenState
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.entity.QuantFilterMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed class FeedsFragmentState(
    open val listOfQuants: List<QuantBase>,
    open var selectedTimeInterval: TimeInterval,
    open var selectedQuantFilterMode: QuantFilterMode,
    open var selectedTextFilter: String,
    open val quantCategoryNames: List<Pair<QuantCategory, String>>
): ScreenState {
    data class EventsListLoading(
        override val listOfQuants: List<QuantBase>,
        override var selectedTimeInterval: TimeInterval,
        override var selectedQuantFilterMode: QuantFilterMode,
        override var selectedTextFilter: String,
        override val quantCategoryNames: List<Pair<QuantCategory, String>>
    ) : FeedsFragmentState(
        listOfQuants,
        selectedTimeInterval,
        selectedQuantFilterMode,
        selectedTextFilter,
        quantCategoryNames
    ) {
        companion object {
            fun updateStateToLoading(state: MutableStateFlow<FeedsFragmentState>) {
                state.update {
                    EventsListLoading(
                        it.listOfQuants,
                        it.selectedTimeInterval,
                        it.selectedQuantFilterMode,
                        it.selectedTextFilter,
                        it.quantCategoryNames
                    )
                }
            }
        }
    }

    data class LoadingEventsListCompleted(
        override val listOfQuants: List<QuantBase>,
        override var selectedTimeInterval: TimeInterval,
        override var selectedQuantFilterMode: QuantFilterMode,
        override var selectedTextFilter: String,
        override val quantCategoryNames: List<Pair<QuantCategory, String>>,
        val listOfEvents: List<EventListItem>,
        val totalPhysicalFound: Double,
        val totalEmotionalFound: Double,
        val totalEvolutionFound: Double,
        val totalStarFound: Double,
        val totalFound: Double
    ) : FeedsFragmentState(
        listOfQuants,
        selectedTimeInterval,
        selectedQuantFilterMode,
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
                _listOfEvents: List<EventListItem>,
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

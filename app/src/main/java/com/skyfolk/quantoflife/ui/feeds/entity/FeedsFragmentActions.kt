package com.skyfolk.quantoflife.ui.feeds.entity

import com.skyfolk.quantoflife.entity.EventBase
import com.skyfolk.quantoflife.shared.presentation.state.ScreenAction
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.entity.QuantFilterMode

sealed class FeedsFragmentActions: ScreenAction {

    data class EditEventAction(val eventId: String): FeedsFragmentActions()
    data class SetSelectedQuantFilterModeAction(val mode: QuantFilterMode): FeedsFragmentActions()
    data class SetTimeIntervalStateAction(val timeInterval: TimeInterval): FeedsFragmentActions()
    data class SetSearchTextAction(val searchText: String): FeedsFragmentActions()
    data class EventEditedAction(val event: EventBase): FeedsFragmentActions()
    data class DeleteEventAction(val event: EventBase): FeedsFragmentActions()
    object RunSearchAction: FeedsFragmentActions()
}
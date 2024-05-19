package com.skyfolk.quantoflife.ui.now.create

import androidx.lifecycle.LiveData
import com.skyfolk.quantoflife.db.EventsStorageInteractor
import com.skyfolk.quantoflife.db.IQuantsStorageInteractor
import com.skyfolk.quantoflife.settings.SettingsInteractor
import com.skyfolk.quantoflife.shared.presentation.vm.BaseViewModel
import com.skyfolk.quantoflife.ui.feeds.entity.FeedsFragmentSingleLifeEvent
import com.skyfolk.quantoflife.ui.feeds.entity.FeedsFragmentState
import com.skyfolk.quantoflife.utils.SingleLiveEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateEventViewModel(
    private val eventsStorageInteractor: EventsStorageInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val quantsStorageInteractor: IQuantsStorageInteractor,
): BaseViewModel<CreateEventActions, CreateEventState, CreateEventSingleLifeEvent>() {

    private val _state = MutableStateFlow<CreateEventState>(
        CreateEventState.CreateEventInProgress(1)
    )
    override val state: StateFlow<CreateEventState> = _state.asStateFlow()

    private val _singleLifeEvent = SingleLiveEvent<CreateEventSingleLifeEvent>()
    override val singleLifeEvent: LiveData<CreateEventSingleLifeEvent> get() = _singleLifeEvent

    override fun proceedAction(action: CreateEventActions) {
    }
}
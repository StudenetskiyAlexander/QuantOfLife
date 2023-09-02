package com.skyfolk.quantoflife.shared.presentation.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.skyfolk.quantoflife.shared.presentation.state.ScreenAction
import com.skyfolk.quantoflife.shared.presentation.state.ScreenState
import com.skyfolk.quantoflife.shared.presentation.state.SingleLifeEvent
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel<A: ScreenAction, S: ScreenState, E: SingleLifeEvent>: ViewModel() {

    abstract val state: StateFlow<S>
    abstract val singleLifeEvent: LiveData<E>

    abstract fun proceedAction(action: A)
}
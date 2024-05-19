package com.skyfolk.quantoflife.ui.now.create

import com.skyfolk.quantoflife.shared.presentation.state.ScreenState

sealed class CreateEventState: ScreenState {

    data class CreateEventInProgress(val something: Int): CreateEventState()
}
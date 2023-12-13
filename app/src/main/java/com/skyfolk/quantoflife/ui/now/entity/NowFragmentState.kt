package com.skyfolk.quantoflife.ui.now.entity

import com.skyfolk.quantoflife.entity.GoalPresentation
import com.skyfolk.quantoflife.shared.presentation.state.ScreenState

sealed class NowFragmentState: ScreenState {
    data class LoadingCompleted(
        val listOfGoals: List<GoalPresentation>
    ) : NowFragmentState()
}

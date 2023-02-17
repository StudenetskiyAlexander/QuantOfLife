package com.skyfolk.quantoflife.ui.entity

import com.skyfolk.quantoflife.ui.adapter.SpinnerSelectableItem

sealed class GraphSelectedYearMode: SpinnerSelectableItem {
    object All: GraphSelectedYearMode()
    data class OnlyYearMode(val year: Int): GraphSelectedYearMode()

    override fun toPresentation() = when (this) {
        All -> "Все годы"
        is OnlyYearMode -> year.toString()
    }
}
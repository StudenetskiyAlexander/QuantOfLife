package com.skyfolk.quantoflife.ui.entity

import com.skyfolk.quantoflife.TypedSealedClass
import com.skyfolk.quantoflife.ui.adapter.SpinnerSelectableItem

sealed class GraphSelectedYearMode: SpinnerSelectableItem, TypedSealedClass {
    object All: GraphSelectedYearMode()
    data class OnlyYearMode(val year: Int): GraphSelectedYearMode()

    override val type = this.javaClass.name

    override fun toPresentation() = when (this) {
        All -> "Все годы"
        is OnlyYearMode -> year.toString()
    }
}
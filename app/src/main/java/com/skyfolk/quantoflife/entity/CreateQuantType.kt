package com.skyfolk.quantoflife.entity

import com.skyfolk.quantoflife.ui.adapter.SpinnerSelectableItem

enum class CreateQuantType : SpinnerSelectableItem {
    RATED, NOTE, MEASURE;

    override fun toPresentation() = when (this) {
        RATED -> "Оцениваемое"
        NOTE -> "Заметка"
        MEASURE -> "Измеряемое"
    }
}
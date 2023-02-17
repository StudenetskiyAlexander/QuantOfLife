package com.skyfolk.quantoflife.ui.entity

import com.skyfolk.quantoflife.TypedSealedClass
import com.skyfolk.quantoflife.entity.QuantBase
import com.skyfolk.quantoflife.ui.adapter.SpinnerSelectableItem

sealed class QuantFilterMode : SpinnerSelectableItem, TypedSealedClass {
    override val type = this.javaClass.name

    object All : QuantFilterMode()

    class OnlySelected(val quant: QuantBase) : QuantFilterMode() {

        override fun equals(other: Any?): Boolean {
            return (other as? OnlySelected)?.quant?.id == this.quant.id
        }

        override fun hashCode(): Int {
            var result = quant.hashCode()
            result = 31 * result + type.hashCode()
            return result
        }
    }

    override fun toPresentation() = when (this) {
        All -> "Все события"
        is OnlySelected -> quant.name
    }
}
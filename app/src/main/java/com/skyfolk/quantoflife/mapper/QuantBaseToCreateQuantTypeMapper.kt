package com.skyfolk.quantoflife.mapper

import com.skyfolk.quantoflife.entity.CreateQuantType
import com.skyfolk.quantoflife.entity.QuantBase

class QuantBaseToCreateQuantTypeMapper: (QuantBase) -> CreateQuantType {
    override fun invoke(quant: QuantBase) = when (quant) {
        is QuantBase.QuantMeasure -> CreateQuantType.MEASURE
        is QuantBase.QuantNote -> CreateQuantType.NOTE
        is QuantBase.QuantRated -> CreateQuantType.RATED
    }
}
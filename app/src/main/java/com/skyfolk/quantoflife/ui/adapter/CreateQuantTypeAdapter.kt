package com.skyfolk.quantoflife.ui.adapter

import android.content.Context
import com.skyfolk.quantoflife.entity.CreateQuantType
import com.skyfolk.quantoflife.ui.entity.QuantFilterMode

class CreateQuantTypeAdapter(
    context: Context,
    listOfModes: List<CreateQuantType>
) : SpinnerBaseAdapter<CreateQuantType>(
    context,
    listOfModes
)
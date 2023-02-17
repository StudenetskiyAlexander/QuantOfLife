package com.skyfolk.quantoflife.ui.adapter

import android.content.Context
import com.skyfolk.quantoflife.ui.entity.QuantFilterMode

class QuantFilterModeAdapter(
    context: Context,
    listOfModes: List<QuantFilterMode>
) : SpinnerBaseAdapter<QuantFilterMode>(
    context,
    listOfModes
)
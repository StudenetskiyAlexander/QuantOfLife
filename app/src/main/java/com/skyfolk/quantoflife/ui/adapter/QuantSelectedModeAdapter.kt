package com.skyfolk.quantoflife.ui.adapter

import android.content.Context
import com.skyfolk.quantoflife.ui.entity.GraphQuantFilterMode

class QuantSelectedModeAdapter(
    context: Context,
    listOfModes: List<GraphQuantFilterMode>
) : SpinnerBaseAdapter<GraphQuantFilterMode>(
    context,
    listOfModes
)
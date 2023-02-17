package com.skyfolk.quantoflife.ui.adapter

import android.content.Context
import com.skyfolk.quantoflife.ui.entity.GraphSelectedYearMode

class GraphSelectedYearModeAdapter(
    context: Context,
    listOfModes: List<GraphSelectedYearMode>
) : SpinnerBaseAdapter<GraphSelectedYearMode>(
    context,
    listOfModes
)
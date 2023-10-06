package com.skyfolk.quantoflife.entity

import androidx.annotation.ColorInt
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import java.util.*

data class Goal(
    var id: String = UUID.randomUUID().toString(),
    var duration: TimeInterval,
    var target: Double,
    var type: QuantCategory
)

data class GoalPresentation(
    var id: String,
    var targetText: String,
    var progress: Int,
    var progressText: String,
    var additionText: String,
    @ColorInt
    var barColor: Int
)

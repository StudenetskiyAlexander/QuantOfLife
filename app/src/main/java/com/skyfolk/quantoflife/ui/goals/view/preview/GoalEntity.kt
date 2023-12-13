package com.skyfolk.quantoflife.ui.goals.view.preview

import com.skyfolk.quantoflife.entity.GoalPresentation

val GOAL_DISPLAYABLE = GoalPresentation(
    id = "someId",
    targetText = "Вы поставили такую-то цель",
    progress = 55,
    progressText = "45 из 90",
    additionText = "Все отлично, продолжайте в том же духе!",
    barColor = 0
)

val GOAL_COMPLETED_DISPLAYABLE = GoalPresentation(
    id = "someId",
    targetText = "Вы поставили такую-то цель",
    progress = 100,
    progressText = "Готово - 100 из 90",
    additionText = "addition text",
    barColor = 0
)
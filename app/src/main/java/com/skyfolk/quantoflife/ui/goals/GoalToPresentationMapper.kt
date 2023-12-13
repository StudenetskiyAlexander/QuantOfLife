package com.skyfolk.quantoflife.ui.goals

import android.content.Context
import com.skyfolk.quantoflife.IDateTimeRepository
import com.skyfolk.quantoflife.R
import com.skyfolk.quantoflife.db.EventsStorageInteractor
import com.skyfolk.quantoflife.db.IQuantsStorageInteractor
import com.skyfolk.quantoflife.entity.Goal
import com.skyfolk.quantoflife.entity.GoalPresentation
import com.skyfolk.quantoflife.feeds.getTotal
import com.skyfolk.quantoflife.settings.SettingsInteractor
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.utils.format
import com.skyfolk.quantoflife.utils.getStartDateCalendar

internal class GoalToPresentationMapper(
    private val context: Context,
    private val quantsStorageInteractor: IQuantsStorageInteractor,
    private val dateTimeRepository: IDateTimeRepository,
    private val settingsInteractor: SettingsInteractor,
    private val eventsStorageInteractor: EventsStorageInteractor
) : suspend (Goal) -> GoalPresentation {
    override suspend fun invoke(goal: Goal): GoalPresentation {
        val goalStartDate = dateTimeRepository.getCalendar().getStartDateCalendar(
            goal.duration,
            settingsInteractor.startDayTime
        ).timeInMillis

        val endDate = dateTimeRepository.getTimeInMillis()
        // Как-будто это не задача этого слоя - считать
        val goalResultList = ArrayList(
            eventsStorageInteractor.getAllEvents()
                .filter { it.date in goalStartDate until endDate })
        val completed = getTotal(
            quantsStorageInteractor.getAllQuantsList(false),
            goalResultList,
            goal.type
        )
        val progress = ((completed / goal.target) * 100).toInt()
        val color = context.resources.getColor(
            when (progress >= 100) {
                true -> R.color.progressCompleted
                false -> R.color.progressPrimary
            },
            context.theme
        )

        val targetText = context.applicationContext.getString(
            R.string.goal_title_with_values,
            goal.target,
            settingsInteractor.categoryNames[goal.type],
            goal.duration.toStringName(
                context.resources.getStringArray(R.array.goal_time_interval)
            )
        )

        val progressText = context.getString(
            when (progress >= 100) {
                true -> R.string.goal_complete
                false -> R.string.goal_complete_part
            },
            completed.format(0), goal.target.format(0)
        )

        val durationInDays = goal.duration.durationInDays(dateTimeRepository)
        val millisecondsInDay = 24 * 60 * 60 * 1000
        val daysGone = ((endDate - goalStartDate) / millisecondsInDay).toInt() + 1

        val additionText =
            if (goal.duration != TimeInterval.All) {
                val futureProgress = completed / daysGone * durationInDays
                if (futureProgress >= goal.target) {
                    context.getString(R.string.goal_progress_ok)
                } else {
                    val needToBeInTarget =
                        (goal.target - futureProgress) * daysGone / durationInDays
                    context.getString(
                        R.string.goal_to_complete_note,
                        needToBeInTarget.format(2)
                    )
                }
            } else ""

        return GoalPresentation(
            id = goal.id,
            targetText = targetText,
            progress = progress,
            progressText = progressText,
            additionText = additionText,
            barColor = color
        )
    }
}
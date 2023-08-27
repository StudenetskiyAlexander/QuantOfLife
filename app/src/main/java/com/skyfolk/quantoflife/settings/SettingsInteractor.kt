package com.skyfolk.quantoflife.settings

import android.content.Context
import com.skyfolk.quantoflife.GraphSelectedMode
import com.skyfolk.quantoflife.R
import com.skyfolk.quantoflife.entity.QuantCategory
import com.skyfolk.quantoflife.meansure.Measure
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.entity.QuantFilterMode
import com.skyfolk.quantoflife.ui.entity.GraphSelectedYearMode
import com.skyfolk.quantoflife.utils.*
import java.util.*


class SettingsInteractor(private val context: Context) {

    companion object {
        const val CATEGORY_NAME_ = "category_name_"

        const val PERIOD_WHILE_SELECTED_CALENDAR_MATTERS = 60 * 60 * 1000
    }

    private val preferences = context.getSharedPreferences("qol_preferences", Context.MODE_PRIVATE)
    private val gson = GsonTypedBuilder.build()

    var lastSelectedCalendar: Calendar
        get() {
            return if (Calendar.getInstance().timeInMillis - lastSelectedCalendarWhenSelected.timeInMillis
                > PERIOD_WHILE_SELECTED_CALENDAR_MATTERS
            ) {
                Calendar.getInstance()
            } else {
                lastSelectedCalendarWhatSelected
            }
        }
        set(value) {
            lastSelectedCalendarWhatSelected = value
            lastSelectedCalendarWhenSelected = Calendar.getInstance()
        }

    private var lastSelectedCalendarWhenSelected by preferences.calendar()
    private var lastSelectedCalendarWhatSelected by preferences.calendar()

    var selectedTimeInterval by preferences.timeInterval(gson, TimeInterval.Week)
    var selectedGraphQuantFirst by preferences.quantFilter(gson, QuantFilterMode.All)
    var selectedGraphQuantSecond by preferences.quantFilter(gson, QuantFilterMode.All)
    var selectedGraphMeasure by preferences.measure(Measure.TotalCount)
    var selectedYearFilter by preferences.graphSelectedYear(gson, GraphSelectedYearMode.All)
    var selectedYearFilter2 by preferences.graphSelectedYear(gson, GraphSelectedYearMode.All)
    var selectedGraphMode by preferences.graphSelectedMode(GraphSelectedMode.Common)

    var feedsSearchText by preferences.string("")
    var feedsQuantFilterMode by preferences.quantFilter(gson, QuantFilterMode.All)
    var feedsTimeIntervalMode by preferences.timeInterval(gson, TimeInterval.All)
    var feedsTimeIntervalSelectedStart by preferences.long(0)
    var feedsTimeIntervalSelectedEnd by preferences.long(0)

    var isOnboardingComplete by preferences.boolean(true)
    var showHidden by preferences.boolean(false)
    var startDayTime by preferences.long(0)

    var categoryNames = mutableMapOf(
        QuantCategory.Physical to getCategoryName(QuantCategory.Physical),
        QuantCategory.Emotion to getCategoryName(QuantCategory.Emotion),
        QuantCategory.Evolution to getCategoryName(QuantCategory.Evolution),
        QuantCategory.All to getCategoryName(QuantCategory.All),
        QuantCategory.Other to getCategoryName(QuantCategory.Other),
        QuantCategory.None to getCategoryName(QuantCategory.None)
    )
        set(value) {
            field = value
            for (v in field) {
                setCategoryName(v.key, v.value)
            }
        }

    private fun getCategoryName(category: QuantCategory): String {
        val default = context.resources.getStringArray(R.array.category_name)[category.ordinal]
        return preferences.getString(CATEGORY_NAME_ + category.name, default) ?: default
    }

    fun getCategoryNames(): ArrayList<Pair<QuantCategory, String>> {
        val result = arrayListOf<Pair<QuantCategory, String>>()

        enumValues<QuantCategory>().forEach {
            val default = context.resources.getStringArray(R.array.category_name)[it.ordinal]
            result.add(
                Pair(
                    it,
                    preferences.getString(CATEGORY_NAME_ + it.name, default) ?: default
                )
            )
        }
        return result
    }

    private fun setCategoryName(category: QuantCategory, name: String) {
        preferences.edit()
            .putString(CATEGORY_NAME_ + category.name, name)
            .apply()
    }
}
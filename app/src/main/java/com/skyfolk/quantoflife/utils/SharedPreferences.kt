package com.skyfolk.quantoflife.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.skyfolk.quantoflife.GraphSelectedMode
import com.skyfolk.quantoflife.meansure.Measure
import com.skyfolk.quantoflife.settings.ReadWriteSealedProperty
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.entity.GraphQuantFilterMode
import com.skyfolk.quantoflife.ui.entity.GraphSelectedYearMode
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun SharedPreferences.string(
    defaultValue: String = "",
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any, String> =
    object : ReadWriteProperty<Any, String> {
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>
        ): String = getString(key(property), defaultValue) ?: defaultValue

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: String
        ) = edit().putString(key(property), value).apply()
    }

fun SharedPreferences.boolean(
    defaultValue: Boolean,
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any, Boolean> =
    object : ReadWriteProperty<Any, Boolean> {
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>
        ): Boolean = getBoolean(key(property), defaultValue) ?: defaultValue

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: Boolean
        ) = edit().putBoolean(key(property), value).apply()
    }

fun SharedPreferences.stringOrNull(
    defaultValue: String?,
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any, String?> =
    object : ReadWriteProperty<Any, String?> {
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>
        ): String? = getString(key(property), defaultValue) ?: defaultValue

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: String?
        ) = edit().putString(key(property), value).apply()
    }

fun SharedPreferences.long(
    defaultValue: Long,
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any, Long> =
    object : ReadWriteProperty<Any, Long> {
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>
        ): Long = getLong(key(property), defaultValue)

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: Long
        ) = edit().putLong(key(property), value).apply()
    }

fun SharedPreferences.calendar(
    defaultValue: Calendar = Calendar.getInstance(),
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any, Calendar> =
    object : ReadWriteProperty<Any, Calendar> {
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>
        ): Calendar {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = getLong(key(property), defaultValue.timeInMillis)
            return calendar
        }

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: Calendar
        ) = edit().putLong(key(property), value.timeInMillis).apply()
    }

fun SharedPreferences.measure(
    defaultValue: Measure,
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any, Measure> =
    object : ReadWriteProperty<Any, Measure> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Measure {
            val stringValue = getString(key(property), null) ?: return defaultValue
            return Measure.valueOf(stringValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Measure) {
            edit {
                putString(key(property), value.name)
            }
        }
    }

fun SharedPreferences.graphSelectedMode(
    defaultValue: GraphSelectedMode,
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any, GraphSelectedMode> =
    object : ReadWriteProperty<Any, GraphSelectedMode> {
        override fun getValue(thisRef: Any, property: KProperty<*>): GraphSelectedMode {
            val stringValue = getString(key(property), null) ?: return defaultValue
            return GraphSelectedMode.valueOf(stringValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: GraphSelectedMode) {
            edit {
                putString(key(property), value.name)
            }
        }
    }

fun SharedPreferences.graphSelectedYear(
    gson: Gson,
    defaultValue: GraphSelectedYearMode,
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteSealedProperty<Any, GraphSelectedYearMode> =
    object : ReadWriteSealedProperty<Any, GraphSelectedYearMode>(
        gson = gson,
        sharedPreferences = this,
        classOf = GraphSelectedYearMode::class.java,
        defaultValue = defaultValue,
        key = key
    ) {}

fun SharedPreferences.timeInterval(
    gson: Gson,
    defaultValue: TimeInterval,
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteSealedProperty<Any, TimeInterval> =
    object : ReadWriteSealedProperty<Any, TimeInterval>(
        gson = gson,
        sharedPreferences = this,
        classOf = TimeInterval::class.java,
        defaultValue = defaultValue,
        key = key
    ) {}

fun SharedPreferences.quantFilter(
    gson: Gson,
    defaultValue: GraphQuantFilterMode,
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteSealedProperty<Any, GraphQuantFilterMode> =
    object : ReadWriteSealedProperty<Any, GraphQuantFilterMode>(
        gson = gson,
        sharedPreferences = this,
        classOf = GraphQuantFilterMode::class.java,
        defaultValue = defaultValue,
        key = key
    ) {}
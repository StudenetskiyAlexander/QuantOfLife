package com.skyfolk.quantoflife.settings

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.skyfolk.quantoflife.TypedSealedClass
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class ReadWriteSealedProperty<Any, T : TypedSealedClass>(
    private val gson: Gson,
    private val sharedPreferences: SharedPreferences,
    private val classOf: Class<T>,
    private val defaultValue: T,
    private val key: (KProperty<*>) -> String = KProperty<*>::name
) : ReadWriteProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        val jsonValue = sharedPreferences.getString(key(property), null) ?: return defaultValue
        return gson.fromJson(jsonValue, classOf)
    }

    override fun setValue(
        thisRef: Any,
        property: KProperty<*>,
        value: T
    ) = sharedPreferences.edit {
        putString(key(property), gson.toJson(value))
    }
}
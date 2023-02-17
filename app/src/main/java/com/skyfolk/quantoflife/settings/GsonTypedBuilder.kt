package com.skyfolk.quantoflife.settings

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import com.skyfolk.quantoflife.GraphSelectedMode
import com.skyfolk.quantoflife.entity.QuantBase
import com.skyfolk.quantoflife.meansure.Measure
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.entity.GraphQuantFilterMode
import com.skyfolk.quantoflife.ui.entity.GraphSelectedYearMode

class GsonTypedBuilder {

    companion object {

        private val graphQuantFilterModeTypeAdapter =
            RuntimeTypeAdapterFactory.of(GraphQuantFilterMode::class.java, "type")
                .registerSubtype(
                    GraphQuantFilterMode.All::class.java,
                    GraphQuantFilterMode.All::class.java.name
                )
                .registerSubtype(
                    GraphQuantFilterMode.OnlySelected::class.java,
                    GraphQuantFilterMode.OnlySelected::class.java.name
                )

        private val quantTypeAdapter = RuntimeTypeAdapterFactory.of(QuantBase::class.java)
            .registerSubtype(QuantBase.QuantRated::class.java)
            .registerSubtype(QuantBase.QuantMeasure::class.java)
            .registerSubtype(QuantBase.QuantNote::class.java)

        private val timeIntervalTypeAdapter = RuntimeTypeAdapterFactory.of(TimeInterval::class.java)
            .registerSubtype(TimeInterval.All::class.java, TimeInterval.All::class.java.name)
            .registerSubtype(TimeInterval.Today::class.java, TimeInterval.Today::class.java.name)
            .registerSubtype(TimeInterval.Week::class.java, TimeInterval.Week::class.java.name)
            .registerSubtype(TimeInterval.Month::class.java, TimeInterval.Month::class.java.name)
            .registerSubtype(TimeInterval.Year::class.java, TimeInterval.Year::class.java.name)
            .registerSubtype(
                TimeInterval.Selected::class.java,
                TimeInterval.Selected::class.java.name
            )

        private val graphSelectedYearModeAdapter =
            RuntimeTypeAdapterFactory.of(GraphSelectedYearMode::class.java)
                .registerSubtype(
                    GraphSelectedYearMode.All::class.java,
                    GraphSelectedYearMode.All::class.java.name
                )
                .registerSubtype(
                    GraphSelectedYearMode.OnlyYearMode::class.java,
                    GraphSelectedYearMode.OnlyYearMode::class.java.name
                )

        fun build(): Gson = GsonBuilder()
            .registerTypeAdapterFactory(graphQuantFilterModeTypeAdapter)
            .registerTypeAdapterFactory(quantTypeAdapter)
            .registerTypeAdapterFactory(timeIntervalTypeAdapter)
            .registerTypeAdapterFactory(graphSelectedYearModeAdapter)
            .create()
    }
}
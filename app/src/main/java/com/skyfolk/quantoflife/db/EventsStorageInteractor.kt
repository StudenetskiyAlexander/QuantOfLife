package com.skyfolk.quantoflife.db

import com.skyfolk.quantoflife.entity.EventBase
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.utils.getEndDateCalendar
import com.skyfolk.quantoflife.utils.getStartDateCalendar
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmQuery
import io.realm.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Calendar

class EventsStorageInteractor(private val dbInteractor: DBInteractor) {
    fun clearDataBase() {
        dbInteractor.getDB().executeTransactionAsync {
            it.deleteAll()
        }
    }

    fun clearEvents() {
        dbInteractor.getDB().executeTransactionAsync {
            it.delete(EventDbEntity::class.java)
            for (quant in it.where(QuantDbEntity::class.java).findAll()) {
                quant.usageCount = 0
            }
        }
    }

    fun addEventToDB(event: EventBase, onComplete: () -> Unit) {
        var rate: Double? = null
        var numericValue: Double? = null

        when (event) {
            is EventBase.EventRated -> {
                rate = event.rate
            }

            is EventBase.EventMeasure -> {
                numericValue = event.value
            }

            is EventBase.EventNote -> {
            }
        }
        val eventDbElement =
            EventDbEntity(event.quantId, event.date, rate, numericValue, event.note, event.isHidden)

        dbInteractor.getDB().executeTransactionAsync({
            val existEvent = existEventOrNull(it, event)
            if (existEvent != null) {
                existEvent.date = event.date
                existEvent.rate = rate
                existEvent.numericValue = numericValue
                existEvent.note = event.note
                existEvent.isHidden = event.isHidden
                existEvent.quantId = event.quantId
            } else {
                it.insertOrUpdate(eventDbElement)
            }
        }, {
            onComplete()
        }, null)
    }

    fun deleteEvent(event: EventBase, onComplete: () -> Unit) {
        dbInteractor.getDB().executeTransactionAsync({
            existEventOrNull(it, event)?.deleteFromRealm()
        }, {
            onComplete()
        }, null)
    }

    fun getAllEventsYears(startDayTime: Long): List<Int> {
        //  TODO Это очень, очень неоптимальный поиск

        val minute = 60 * 1000
        val hour = 60 * minute

        return dbInteractor
            .getDB()
            .where(EventDbEntity::class.java)
            .findAll()
            .map { it.date }
            .sortedBy { it }
            .map {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it

                val dayTime: Long =
                    (calendar[Calendar.HOUR_OF_DAY] * hour + calendar[Calendar.MINUTE] * minute +
                            calendar[Calendar.SECOND] * 1000).toLong()
                if (dayTime < startDayTime) {
                    calendar[Calendar.DAY_OF_YEAR]--
                }

                calendar.getStartDateCalendar(TimeInterval.Year, 0)[Calendar.YEAR]
            }
            .distinct()
    }

    fun getAllEventsAsFlow(includeHidden: Boolean = true): Flow<List<EventBase>> =
        handlerAndReturnListFlow {
            dbInteractor
                .getDB()
                .where(EventDbEntity::class.java)
        }.flowOn(Dispatchers.IO)
            .map { list ->
                list
                    .filter { !it.isHidden || includeHidden }
                    .map { eventDbEntity ->
                        when {
                            (eventDbEntity.rate != null) -> EventBase.EventRated(
                                eventDbEntity.id,
                                eventDbEntity.quantId,
                                eventDbEntity.date,
                                eventDbEntity.note,
                                eventDbEntity.rate!!
                            ).apply { isHidden = eventDbEntity.isHidden }

                            (eventDbEntity.numericValue != null) -> EventBase.EventMeasure(
                                eventDbEntity.id,
                                eventDbEntity.quantId,
                                eventDbEntity.date,
                                eventDbEntity.note,
                                eventDbEntity.numericValue!!
                            ).apply { isHidden = eventDbEntity.isHidden }

                            else -> EventBase.EventNote(
                                eventDbEntity.id,
                                eventDbEntity.quantId,
                                eventDbEntity.date,
                                eventDbEntity.note
                            ).apply { isHidden = eventDbEntity.isHidden }
                        }
                    }
            }


    fun getAllEvents(includeHidden: Boolean = true): ArrayList<EventBase> {
        val result = ArrayList<EventBase>()

        dbInteractor.getDB().where(EventDbEntity::class.java).findAll()
            .sortedBy { it.date }
            .filter {
                !it.isHidden || includeHidden
            }
            .forEach { eventDbEntity ->
                when {
                    (eventDbEntity.rate != null) -> {
                        result.add(
                            EventBase.EventRated(
                                eventDbEntity.id,
                                eventDbEntity.quantId,
                                eventDbEntity.date,
                                eventDbEntity.note,
                                eventDbEntity.rate!!
                            ).apply { isHidden = eventDbEntity.isHidden }
                        )
                    }

                    (eventDbEntity.numericValue != null) -> {
                        result.add(
                            EventBase.EventMeasure(
                                eventDbEntity.id,
                                eventDbEntity.quantId,
                                eventDbEntity.date,
                                eventDbEntity.note,
                                eventDbEntity.numericValue!!
                            ).apply { isHidden = eventDbEntity.isHidden }
                        )
                    }

                    else -> {
                        result.add(
                            EventBase.EventNote(
                                eventDbEntity.id,
                                eventDbEntity.quantId,
                                eventDbEntity.date,
                                eventDbEntity.note
                            ).apply { isHidden = eventDbEntity.isHidden }
                        )
                    }
                }
            }

        return result
    }

    fun getAllEventsByMonth(
        calendar: Calendar,
        includeHidden: Boolean = true
    ): ArrayList<EventBase> {
        val result = ArrayList<EventBase>()
        val start = calendar.getStartDateCalendar(TimeInterval.Month, 0).timeInMillis
        val end = calendar.getEndDateCalendar(TimeInterval.Month, 0).timeInMillis

        dbInteractor.getDB().freeze().where(EventDbEntity::class.java).between("date", start, end)
            .findAll()
            .sortedBy { it.date }
            .filter {
                !it.isHidden || includeHidden
            }
            .forEach { eventDbEntity ->
                when {
                    (eventDbEntity.rate != null) -> {
                        result.add(
                            EventBase.EventRated(
                                eventDbEntity.id,
                                eventDbEntity.quantId,
                                eventDbEntity.date,
                                eventDbEntity.note,
                                eventDbEntity.rate!!
                            ).apply { isHidden = eventDbEntity.isHidden }
                        )
                    }

                    (eventDbEntity.numericValue != null) -> {
                        result.add(
                            EventBase.EventMeasure(
                                eventDbEntity.id,
                                eventDbEntity.quantId,
                                eventDbEntity.date,
                                eventDbEntity.note,
                                eventDbEntity.numericValue!!
                            ).apply { isHidden = eventDbEntity.isHidden }
                        )
                    }

                    else -> {
                        result.add(
                            EventBase.EventNote(
                                eventDbEntity.id,
                                eventDbEntity.quantId,
                                eventDbEntity.date,
                                eventDbEntity.note
                            ).apply { isHidden = eventDbEntity.isHidden }
                        )
                    }
                }
            }

        return result
    }

    suspend fun alreadyHaveEvent(event: EventBase): Boolean = withContext(Dispatchers.IO) {
        return@withContext dbInteractor
            .getDB()
            .freeze()
            .where(EventDbEntity::class.java)
            .equalTo("id", event.id)
            .findFirst() != null
    }

    private fun existEventOrNull(realm: Realm, event: EventBase): EventDbEntity? {
        return realm.where(EventDbEntity::class.java)
            .equalTo("id", event.id)
            .findFirst()
    }

    private fun handlerAndReturnListFlow(block: () -> RealmQuery<EventDbEntity>): Flow<List<EventDbEntity>> =
        callbackFlow {

            val results = block().findAllAsync()!!
            val realm = results.realm!!

            val listener = RealmChangeListener<RealmResults<EventDbEntity>> { t ->
                this.trySend(t).isSuccess
            }

            results.addChangeListener(listener)
            this.trySend(results).isSuccess

            awaitClose {
                if (!realm.isClosed) {
                    results.removeChangeListener(listener)
                    realm.close()
                }
            }
        }.flowOn(Dispatchers.Main)
}
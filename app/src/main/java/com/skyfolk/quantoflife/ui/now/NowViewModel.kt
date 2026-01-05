package com.skyfolk.quantoflife.ui.now

import EventOnPicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyfolk.quantoflife.IDateTimeRepository
import com.skyfolk.quantoflife.db.EventsStorageInteractor
import com.skyfolk.quantoflife.db.IGoalStorageInteractor
import com.skyfolk.quantoflife.db.IQuantsStorageInteractor
import com.skyfolk.quantoflife.entity.EventBase
import com.skyfolk.quantoflife.entity.Goal
import com.skyfolk.quantoflife.entity.GoalPresentation
import com.skyfolk.quantoflife.entity.QuantBase
import com.skyfolk.quantoflife.feeds.getTotal
import com.skyfolk.quantoflife.import.ImportInteractor
import com.skyfolk.quantoflife.mapper.QuantBaseToCreateQuantTypeMapper
import com.skyfolk.quantoflife.mapper.TimeIntervalToPeriodInMillisMapper
import com.skyfolk.quantoflife.settings.SettingsInteractor
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.create_quant.CreateQuantDialogFragment
import com.skyfolk.quantoflife.ui.goals.CreateGoalDialogFragment
import com.skyfolk.quantoflife.ui.goals.GoalToPresentationMapper
import com.skyfolk.quantoflife.ui.now.date_picker.MonthEventsForPickerProvider
import com.skyfolk.quantoflife.utils.SingleLiveEvent
import com.skyfolk.quantoflife.utils.getStartDateCalendar
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class NowViewModel(
    private val timeIntervalToPeriodInMillisMapper: TimeIntervalToPeriodInMillisMapper,
    private val quantsStorageInteractor: IQuantsStorageInteractor,
    private val eventsStorageInteractor: EventsStorageInteractor,
    private val goalStorageInteractor: IGoalStorageInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val dateTimeRepository: IDateTimeRepository,
    private val importInteractor: ImportInteractor,
    private val quantBaseToCreateQuantTypeMapper: QuantBaseToCreateQuantTypeMapper,
    private val goalToPresentationMapper: GoalToPresentationMapper
) : ViewModel(), INowViewModel {
    private val _toastState = SingleLiveEvent<String>()
    val toastState: LiveData<String> get() = _toastState

    private val _dialogState = SingleLiveEvent<DialogFragment>()
    val dialogState: LiveData<DialogFragment> get() = _dialogState

    private val _listOfQuants = MutableLiveData<List<QuantBase>>().apply {
        value = quantsStorageInteractor.getAllQuantsList(false)
    }
    val listOfQuants: LiveData<List<QuantBase>> = _listOfQuants

    val todayTotal = eventsStorageInteractor.getAllEventsAsFlow().map { list ->
        val startDate = dateTimeRepository.getCalendar().getStartDateCalendar(
            TimeInterval.Today,
            settingsInteractor.startDayTime
        ).timeInMillis
        val endDate = dateTimeRepository.getTimeInMillis()

        getTotal(
            quantsStorageInteractor.getAllQuantsList(false),
            list.filter { it.date in startDate until endDate }
        )
    }

    val todayEvents = eventsStorageInteractor
        .getAllEventsAsFlow()
        .map {
            it
                .filter {
                    it.date in timeIntervalToPeriodInMillisMapper.invoke(
                        TimeInterval.Today,
                        settingsInteractor.startDayTime
                    )
                }.mapNotNull { event ->
                    val quants = quantsStorageInteractor.getAllQuantsList(true)
                    val quant = quants.firstOrNull { it.id == event.quantId }
                    quant?.let {
                        EventOnPicker(
                            time = event.date,
                            comment = getEventNote(event),
                            iconName = it.icon
                        )
                    }
                }.sortedBy { it.time }
        }

    private val _listOfGoals = MutableLiveData<List<GoalPresentation>>().apply {
        value = arrayListOf()
    }
    val listOfGoal: LiveData<List<GoalPresentation>> = _listOfGoals

    init {
        if (quantsStorageInteractor.getAllQuantsList(false).isEmpty()) {
            viewModelScope.launch {
                importInteractor.importEventsFromRaw {
                    _listOfQuants.value = quantsStorageInteractor.getAllQuantsList(false)
                }
            }
        }

        updateTodayTotal()
    }

    override fun openCreateNewQuantDialog(existQuant: QuantBase?) {
        val dialog = CreateQuantDialogFragment(
            existQuant,
            settingsInteractor,
            quantBaseToCreateQuantTypeMapper
        )
        dialog.setDialogListener(object : CreateQuantDialogFragment.DialogListener {
            override fun onConfirm(quant: QuantBase) {
                quantsStorageInteractor.addQuantToDB(quant) {
                    _listOfQuants.value = quantsStorageInteractor.getAllQuantsList(false)
                    updateTodayTotal()
                }
            }

            override fun onDelete(quant: QuantBase) {
                quantsStorageInteractor.deleteQuant(quant) {
                    _listOfQuants.value = quantsStorageInteractor.getAllQuantsList(false)
                }
            }

            override fun onDecline() {}
        })
        _dialogState.value = dialog
    }

    override fun openCreateNewGoalDialog(existGoalId: String?) {
        val dialog =
            CreateGoalDialogFragment(existGoalId, settingsInteractor, goalStorageInteractor)
        dialog.setDialogListener(object : CreateGoalDialogFragment.DialogListener {
            override fun onConfirm(goal: Goal) {
                goalStorageInteractor.addGoalToDB(goal)
                updateTodayTotal()
            }

            override fun onDelete(goal: Goal) {
                goalStorageInteractor.deleteGoal(goal)
                updateTodayTotal()
            }

            override fun onDecline() {}
        })
        _dialogState.value = dialog
    }

    override fun onEventCreated(event: EventBase) {
        eventsStorageInteractor.addEventToDB(event) {
            quantsStorageInteractor.incrementQuantUsage(event.quantId)
            _listOfQuants.value = quantsStorageInteractor.getAllQuantsList(false)
            updateTodayTotal()
        }
    }

    override fun onEventCanceled(event: EventBase) {
        eventsStorageInteractor.deleteEvent(event) {
            quantsStorageInteractor.decrementQuantUsage(event.quantId)
            _listOfQuants.value = quantsStorageInteractor.getAllQuantsList(false)
            updateTodayTotal()
        }
    }

    private fun updateTodayTotal() {
        viewModelScope.launch {
            _listOfGoals.value =
                goalStorageInteractor.getListOfGoals()
                    .map { goalToPresentationMapper.invoke(it) }
        }
    }

    private fun getEventNote(event: EventBase): String = when (event) {
        is EventBase.EventMeasure -> "${event.value}, ${event.note} "
        is EventBase.EventNote -> event.note
        is EventBase.EventRated -> "${event.rate}★, ${event.note}"
    }
}
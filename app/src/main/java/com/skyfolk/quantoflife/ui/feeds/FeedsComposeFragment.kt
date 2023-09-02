package com.skyfolk.quantoflife.ui.feeds

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skyfolk.quantoflife.databinding.FeedsFragmentComposeBinding
import com.skyfolk.quantoflife.entity.EventBase
import com.skyfolk.quantoflife.shared.presentation.view.BaseComposeFragment
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.entity.QuantFilterMode
import com.skyfolk.quantoflife.ui.feeds.entity.FeedsFragmentActions
import com.skyfolk.quantoflife.ui.feeds.entity.FeedsFragmentActions.*
import com.skyfolk.quantoflife.ui.feeds.entity.FeedsFragmentSingleLifeEvent
import com.skyfolk.quantoflife.ui.feeds.entity.FeedsFragmentState
import com.skyfolk.quantoflife.ui.now.CreateEventDialogFragment
import com.skyfolk.quantoflife.ui.statistic.NavigateToFeedEvent
import com.skyfolk.quantoflife.ui.theme.QuantOfLifeMainTheme
import com.skyfolk.quantoflife.utils.setOnHideByTimeout
import com.skyfolk.quantoflife.R
import com.skyfolk.quantoflife.shared.presentation.view.withNotNull
import com.skyfolk.quantoflife.ui.feeds.view.EventsList
import com.skyfolk.quantoflife.ui.feeds.view.FilterBlock
import com.skyfolk.quantoflife.ui.feeds.view.SeparatorLine
import com.skyfolk.quantoflife.ui.feeds.view.TotalValues
import com.skyfolk.quantoflife.ui.feeds.vm.FeedsViewModel
import kotlinx.coroutines.flow.StateFlow
import org.koin.android.viewmodel.ext.android.viewModel

class FeedsComposeFragment :
    BaseComposeFragment<FeedsFragmentActions, FeedsFragmentState, FeedsFragmentSingleLifeEvent>() {
    override val viewModel: FeedsViewModel by viewModel()
    override val binding by dataBinding<FeedsFragmentComposeBinding>(
        layoutId = R.layout.feeds_fragment_compose
    )

    override fun onResume() {
        super.onResume()

        arguments?.let {
            val start = it.getLong(NavigateToFeedEvent.START_DATE_KEY)
            val end = it.getLong(NavigateToFeedEvent.END_DATE_KEY)

            if (start != 0L && end != 0L) {
                viewModel.proceedAction(
                    SetTimeIntervalStateAction(TimeInterval.Selected(start, end))
                )
            } else {
                viewModel.proceedAction(RunSearchAction)
            }
        }
    }

    override fun proceedScreenState(stateFlow: StateFlow<FeedsFragmentState>) =
        withNotNull(binding) {
            composeView.setContent {

                val state by viewModel.state.collectAsState()

                QuantOfLifeMainTheme {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(vertical = 5.dp)
                    ) {
                        when (state) {
                            is FeedsFragmentState.EventsListLoading -> {
                                progress.visibility = View.VISIBLE
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(2f)
                                        .fillMaxWidth()
                                ) {
                                    //CircularProgressIndicator()
                                }
                            }

                            is FeedsFragmentState.LoadingEventsListCompleted -> {
                                progress.visibility = View.GONE

                                EventsList(
                                    modifier = Modifier.weight(2f),
                                    events = (state as FeedsFragmentState.LoadingEventsListCompleted).listOfEvents.reversed()
                                ) { id ->
                                    viewModel.proceedAction(EditEventAction(id))
                                }

                                SeparatorLine()
                            }
                        }

                        // Common state
                        state.let { state ->
                            val listOfQuantFilterModes: MutableList<QuantFilterMode> =
                                state.listOfQuants.map { QuantFilterMode.OnlySelected(it) }
                                    .toMutableList()
                            listOfQuantFilterModes.add(0, QuantFilterMode.All)

                            TotalValues(state = state)

                            FilterBlock(
                                listOfQuantFilterModes = listOfQuantFilterModes,
                                selectedQuantFilterMode = state.selectedQuantFilterMode,
                                onSelectQuantFilterMode = { mode ->
                                    viewModel.proceedAction(
                                        SetSelectedQuantFilterModeAction(mode)
                                    )
                                },
                                listOfTimeInterval = getTimeIntervalModes(),
                                selectedTimeInterval = state.selectedTimeInterval,
                                selectedTextFilter = state.selectedTextFilter,
                                onSelectTimeInterval = { timeInterval ->
                                    viewModel.proceedAction(SetTimeIntervalStateAction(timeInterval))
                                },
                                onTextSearchEnter = {
                                    viewModel.proceedAction(SetSearchTextAction(it))
                                },
                                modifier = Modifier
                            )
                        }
                    }
                }
            }
        }

    override fun proceedSingleLifeEvent(event: FeedsFragmentSingleLifeEvent) {
        when (event) {
            is FeedsFragmentSingleLifeEvent.ShowEditEventDialog -> {
                val dialog = CreateEventDialogFragment(event.quant, event.event)
                dialog.setDialogListener(object :
                    CreateEventDialogFragment.DialogListener {
                    override fun onConfirm(event: EventBase, name: String) {
                        val snackBar = com.google.android.material.snackbar.Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            "Событие '${name}' изменено",
                            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                        )
                        snackBar.setAction("Отмена") {
                        }
                        snackBar.setOnHideByTimeout {
                            viewModel.proceedAction(EventEditedAction(event))
                        }
                        snackBar.show()
                    }

                    override fun onDecline() {
                    }

                    override fun onDelete(event: EventBase, name: String) {
                        val snackBar = com.google.android.material.snackbar.Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            "Событие '${name}' удалено",
                            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                        )
                        snackBar.setAction("Отмена") {
                        }
                        snackBar.setOnHideByTimeout {
                            viewModel.proceedAction(DeleteEventAction(event))
                        }
                        snackBar.show()
                    }
                })
                dialog.show(requireActivity().supportFragmentManager, dialog.tag)
            }
        }
    }

    private fun getTimeIntervalModes(): List<TimeInterval> {
        val storedTimeRange = viewModel.getStoredSelectedTimeInterval()

        return listOf(
            TimeInterval.All,
            TimeInterval.Today,
            TimeInterval.Week,
            TimeInterval.Month,
            TimeInterval.Year,
            TimeInterval.Selected(storedTimeRange.first, storedTimeRange.last)
        )
    }
}
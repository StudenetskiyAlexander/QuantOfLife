package com.skyfolk.quantoflife.ui.feeds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.skyfolk.quantoflife.QLog
import com.skyfolk.quantoflife.databinding.FeedsFragmentComposeBinding
import com.skyfolk.quantoflife.entity.EventBase
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.entity.QuantFilterMode
import com.skyfolk.quantoflife.ui.now.CreateEventDialogFragment
import com.skyfolk.quantoflife.ui.statistic.NavigateToFeedEvent
import com.skyfolk.quantoflife.ui.theme.ComposeFlowTestTheme
import com.skyfolk.quantoflife.utils.setOnHideByTimeout
import com.skyfolk.quantoflife.utils.toDate
import org.koin.android.viewmodel.ext.android.viewModel

class FeedsComposeFragment : Fragment() {
    private val viewModel: FeedsViewModel by viewModel()
    private lateinit var binding: FeedsFragmentComposeBinding

    override fun onResume() {
        super.onResume()

        arguments?.let {
            val start = it.getLong(NavigateToFeedEvent.START_DATE_KEY)
            val end = it.getLong(NavigateToFeedEvent.END_DATE_KEY)

            if (start != 0L && end != 0L) {
                QLog.d("skyfolk-graph", "event from graph ${start.toDate()} to ${end.toDate()}")
                viewModel.setTimeIntervalState(TimeInterval.Selected(start, end))
            } else {
                viewModel.runSearch()
            }
        }
    }

    @ExperimentalComposeUiApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FeedsFragmentComposeBinding.inflate(inflater, container, false)
            .apply {
                composeView.setContent {

                    val state by viewModel.state.collectAsState()

                    ComposeFlowTestTheme {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(vertical = 5.dp)
                        ) {
                            when (state) {
                                is FeedsFragmentState.EventsListLoading -> {
                                    binding.progress.visibility = View.VISIBLE
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
                                    binding.progress.visibility = View.GONE

                                    EventsList(
                                        modifier = Modifier.weight(2f),
                                        events = (state as FeedsFragmentState.LoadingEventsListCompleted).listOfEvents.reversed()
                                    ) { id ->
                                        viewModel.editEvent(id)
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

                                TotalValues(state, modifier = Modifier)

                                FilterBlock(
                                    listOfQuantFilterModes = listOfQuantFilterModes,
                                    selectedQuantFilterMode = state.selectedQuantFilterMode,
                                    onSelectQuantFilterMode = { mode ->
                                        viewModel.setSelectedQuantFilterMode(mode)
                                    },
                                    listOfTimeInterval = getTimeIntervalModes(),
                                    selectedTimeInterval = state.selectedTimeInterval,
                                    selectedTextFilter = state.selectedTextFilter,
                                    onSelectTimeInterval = { timeInterval ->
                                        viewModel.setTimeIntervalState(timeInterval)
                                    },
                                    onTextSearchEnter = {
                                        viewModel.setSearchText(it)
                                    },
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                }
            }

        viewModel.singleLifeEvent.observe(
            viewLifecycleOwner
        ) { event ->
            when (event) {
                is FeedsFragmentSingleLifeEvent.ShowEditEventDialog -> {
                    val dialog = CreateEventDialogFragment(event.quant, event.event)
                    dialog.setDialogListener(object :
                        CreateEventDialogFragment.DialogListener {
                        override fun onConfirm(event: EventBase, name: String) {
                            val snackBar = com.google.android.material.snackbar.Snackbar.make(
                                requireActivity().findViewById(android.R.id.content),
                                "?????????????? '${name}' ????????????????",
                                com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                            )
                            snackBar.setAction("????????????") {
                            }
                            snackBar.setOnHideByTimeout {
                                viewModel.eventEdited(event)
                            }
                            snackBar.show()
                        }

                        override fun onDecline() {
                        }

                        override fun onDelete(event: EventBase, name: String) {
                            val snackBar = com.google.android.material.snackbar.Snackbar.make(
                                requireActivity().findViewById(android.R.id.content),
                                "?????????????? '${name}' ??????????????",
                                com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                            )
                            snackBar.setAction("????????????") {
                            }
                            snackBar.setOnHideByTimeout {
                                viewModel.deleteEvent(event)
                            }
                            snackBar.show()
                        }
                    })
                    dialog.show(requireActivity().supportFragmentManager, dialog.tag)
                }
            }
        }

        return binding.root
    }

    private fun getTimeIntervalModes() : List<TimeInterval> {
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
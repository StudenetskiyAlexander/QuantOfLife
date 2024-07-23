package com.skyfolk.quantoflife.ui.now.create

import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skyfolk.quantoflife.R
import com.skyfolk.quantoflife.databinding.CreateEventComposeBinding
import com.skyfolk.quantoflife.databinding.FeedsFragmentComposeBinding
import com.skyfolk.quantoflife.entity.EventBase
import com.skyfolk.quantoflife.entity.QuantBase
import com.skyfolk.quantoflife.shared.presentation.view.BaseComposeBottomSheetFragment
import com.skyfolk.quantoflife.shared.presentation.view.BaseComposeFragment
import com.skyfolk.quantoflife.shared.presentation.view.withNotNull
import com.skyfolk.quantoflife.ui.now.CreateEventDialogFragment
import com.skyfolk.quantoflife.ui.statistic.NavigateToFeedEvent
import com.skyfolk.quantoflife.ui.theme.QuantOfLifeMainTheme
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.compose.viewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateEventComposeFragment(
    val quant: QuantBase,
    val dialogListener: CreateEventDialogFragment.DialogListener,
    private val existEvent: EventBase? = null
) :
    BaseComposeBottomSheetFragment<CreateEventActions, CreateEventState, CreateEventSingleLifeEvent>() {
    override val viewModel: CreateEventViewModel by viewModel()
    override val binding by dataBinding<CreateEventComposeBinding>(
        layoutId = R.layout.create_event_compose
    )

    override fun onResume() {
        super.onResume()

        arguments?.let {
            val start = it.getLong(NavigateToFeedEvent.START_DATE_KEY)
            val end = it.getLong(NavigateToFeedEvent.END_DATE_KEY)

//                viewModel.proceedAction(RunSearchAction)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun proceedScreenState(stateFlow: StateFlow<CreateEventState>) =
        withNotNull(binding) {
            createEventComposeView.setContent {

                val state by stateFlow.collectAsState()

                QuantOfLifeMainTheme {
                    val sheetState = rememberModalBottomSheetState()
                    val scope = rememberCoroutineScope()
                    var showBottomSheet by remember { mutableStateOf(true) }

                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                showBottomSheet = false
                            },
                            sheetState = sheetState
                        ) {
                            // Sheet content
                            Button(onClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false
                                        dismiss()
                                    }
                                }
                            }) {
                                Text("Hide bottom sheet")
                            }
                        }
                    }
                }
            }
        }

    override fun proceedSingleLifeEvent(event: CreateEventSingleLifeEvent) {}
}
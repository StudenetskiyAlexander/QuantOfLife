package com.skyfolk.quantoflife.ui.now

import EventOnPicker
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skyfolk.quantoflife.databinding.SelectDateTimeFragmentBinding
import com.skyfolk.quantoflife.db.EventsStorageInteractor
import com.skyfolk.quantoflife.db.IQuantsStorageInteractor
import com.skyfolk.quantoflife.entity.toDisplayableEvents
import com.skyfolk.quantoflife.ui.now.date_picker.DateTimePicker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.Calendar

class SelectDateTimeFragment(
    private val lastCalendar: Calendar,
    private val onDateSelected: (Calendar?) -> Unit
) : BottomSheetDialogFragment() {

    private val eventsStorageInteractor: EventsStorageInteractor by inject()
    private val quantsStorageInteractor: IQuantsStorageInteractor by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = SelectDateTimeFragmentBinding.inflate(inflater, container, false)

//        lifecycleScope.launch {
//            val events = eventsStorageInteractor.getAllEventsByMonth(Calendar.getInstance())
//            val quants = quantsStorageInteractor.getAllQuantsList(true)
//
//            eventsToShow.value = events.mapNotNull { event ->
//                val quant = quants.firstOrNull { it.id == event.quantId }
//                quant?.let {
//                    EventOnPicker(
//                        time = event.date,
//                        iconName = it.icon
//                    )
//                }
//            }
//        }

        binding.createEventDatePicker.setContent {
            DateTimePicker(
                onDateSelected = { date ->
                    onDateSelected(date)
                    dismiss()
                },
                startDate = lastCalendar
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
    }
}
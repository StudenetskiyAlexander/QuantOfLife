package com.skyfolk.quantoflife.ui.now

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skyfolk.quantoflife.databinding.SelectDateTimeFragmentBinding
import com.skyfolk.quantoflife.ui.now.date_picker.DateTimePicker
import java.util.Calendar

class SelectDateTimeFragment(
    private val lastCalendar: Calendar,
    private val onlyDataWithNoTime: Boolean = false,
    private val onDateSelected: (Calendar?) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = SelectDateTimeFragmentBinding.inflate(inflater, container, false)

        binding.createEventDatePicker.setContent {
            DateTimePicker(
                onDateSelected = { date ->
                    onDateSelected(date)
                    dismiss()
                },
                onlyDataWithoutTime = onlyDataWithNoTime,
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
package com.skyfolk.quantoflife.ui.now

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skyfolk.quantoflife.databinding.SelectDateTimeFragmentBinding
import com.skyfolk.quantoflife.ui.now.date_picker.DateTimePicker
import java.util.Calendar

class SelectDateTimeFragment(private val onDateSelected: (Calendar) -> Unit) :
    BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = SelectDateTimeFragmentBinding.inflate(inflater, container, false)

        binding.createEventDatePicker.setContent {
            DateTimePicker(
                onDateSelected = { date -> onDateSelected(date) },
//                    currentDate =
            )
        }
        return binding.root
    }
}
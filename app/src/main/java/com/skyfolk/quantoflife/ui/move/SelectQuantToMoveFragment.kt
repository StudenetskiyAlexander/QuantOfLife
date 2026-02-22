package com.skyfolk.quantoflife.ui.move

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skyfolk.quantoflife.databinding.SelectQuantToMoveFragmentBinding
import com.skyfolk.quantoflife.entity.QuantBase
import com.skyfolk.quantoflife.ui.theme.QuantOfLifeMainTheme

class SelectQuantToMoveFragment(
    private val quants: List<QuantBase>,
    private val onQuantSelected: (QuantBase?) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = SelectQuantToMoveFragmentBinding.inflate(inflater, container, false)

        binding.quantToMovePicker.setContent {
            QuantOfLifeMainTheme {
                Column {
                    Text(
                        "Выберите квант для смены",
                        color = Color.White,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 10.dp,
                                bottom = 10.dp,
                            )
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight()
                    ) {
                        quants.forEach {
                            item {
                                DropdownMenuItem(onClick = {
                                    onQuantSelected(it)
                                    dismiss()
                                }) {
                                    androidx.compose.material.Text(text = it.name)
                                }
                            }
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet =
            dialog?.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
        BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
    }
}
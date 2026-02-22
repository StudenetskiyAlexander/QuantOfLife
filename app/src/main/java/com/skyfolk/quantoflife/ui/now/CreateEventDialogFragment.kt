package com.skyfolk.quantoflife.ui.now

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.skyfolk.quantoflife.IDateTimeRepository
import com.skyfolk.quantoflife.R
import com.skyfolk.quantoflife.databinding.CreateEventDialogBinding
import com.skyfolk.quantoflife.db.IQuantsStorageInteractor
import com.skyfolk.quantoflife.db.QuantsStorageInteractor
import com.skyfolk.quantoflife.entity.EventBase
import com.skyfolk.quantoflife.entity.QuantBase
import com.skyfolk.quantoflife.settings.SettingsInteractor
import com.skyfolk.quantoflife.ui.move.SelectQuantToMoveFragment
import com.skyfolk.quantoflife.utils.toDate
import org.koin.android.ext.android.inject
import java.util.Calendar

private const val SEEKBAR_MULTIPLIER = 10

class CreateEventDialogFragment(val quant: QuantBase, private val existEvent: EventBase? = null) :
    BottomSheetDialogFragment() {
    private var dialogListener: DialogListener? = null

    private lateinit var binding: CreateEventDialogBinding

    private val dateTimeRepository: IDateTimeRepository by inject()
    private val settingsInteractor: SettingsInteractor by inject()
    private val quantsStorageInteractor: IQuantsStorageInteractor by inject()
    private val calendar = dateTimeRepository.getCalendar()

    private var isHidden = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CreateEventDialogBinding.inflate(inflater, container, false)
        val imageResource = requireContext().resources.getIdentifier(
            quant.icon,
            "drawable",
            requireContext().packageName
        )
        if (imageResource != 0) {
            binding.quantImage.setImageResource(imageResource)
        } else {
            binding.quantImage.setImageResource(
                requireContext().resources.getIdentifier(
                    "quant_default",
                    "drawable",
                    requireContext().packageName
                )
            )
        }
        binding.eventName.text = quant.name
        binding.eventDescription.text = quant.description

        binding.eventDateChoiceButton.setOnClickListener {
            val lastCalendar = settingsInteractor.lastSelectedCalendar
            val dialog = SelectDateTimeFragment(lastCalendar) {
                it?.let { onDateTimeSelected(it) }
            }
            val fm: FragmentManager = requireActivity().supportFragmentManager
            dialog.show(fm, dialog.tag)
        }

        binding.eventHiddenButton.setOnClickListener {
            isHidden = !isHidden
            updateIsHiddenIcon(isHidden)
        }

        when (existEvent == null) {
            true -> binding.buttonMove.isVisible = false
            false -> {
                binding.buttonMove.setOnClickListener {
                    val fm: FragmentManager = requireActivity().supportFragmentManager
                    val quants = quantsStorageInteractor.getAllQuantsList(false, false)
                    val dialog = SelectQuantToMoveFragment(quants) {
                        it?.let {
                            dismiss()
                            val event = when (it) {
                                is QuantBase.QuantMeasure -> EventBase.EventMeasure(
                                    id = existEvent.id,
                                    quantId = it.id,
                                    date = existEvent.date,
                                    note = existEvent.note,
                                    value = when (existEvent) {
                                        is EventBase.EventMeasure -> existEvent.value
                                        else -> 0.0
                                    }
                                )

                                is QuantBase.QuantNote -> EventBase.EventNote(
                                    id = existEvent.id,
                                    quantId = it.id,
                                    date = existEvent.date,
                                    note = existEvent.note,
                                )

                                is QuantBase.QuantRated -> EventBase.EventRated(
                                    id = existEvent.id,
                                    quantId = it.id,
                                    date = existEvent.date,
                                    note = existEvent.note,
                                    rate = when (existEvent) {
                                        is EventBase.EventRated -> existEvent.rate
                                        else -> 0.0
                                    },
                                )
                            }
                            val listener = object : DialogListener {
                                override fun onConfirm(event: EventBase, name: String) {
                                    this@CreateEventDialogFragment.dialogListener?.onConfirm(
                                        event,
                                        name
                                    )
                                }

                                override fun onDecline() {}

                                override fun onDelete(event: EventBase, name: String) {}
                            }
                            val dialog = CreateEventDialogFragment(it, event)
                            dialog.setDialogListener(listener)
                            dialog.show(fm, dialog.tag)
                        }
                    }
                    dialog.show(fm, dialog.tag)
                }
            }
        }

        var seekBarMultiplier = 1.0
        when (quant) {
            is QuantBase.QuantNote -> {
                binding.eventRating.visibility = View.GONE
                binding.eventRatingNumericLayout.visibility = View.GONE
            }

            is QuantBase.QuantRated -> {
                binding.eventRating.visibility = View.VISIBLE
                binding.eventRatingNumericLayout.visibility = View.GONE
            }

            is QuantBase.QuantMeasure -> {
                binding.eventRating.visibility = View.GONE
                binding.eventRatingNumericLayout.visibility = View.VISIBLE
                seekBarMultiplier = quant.maxSize.toDouble() / 100
            }
        }

        binding.eventRatingNumeric.addTextChangedListener {
            val value = it.toString().toDoubleOrNull() ?: 0.0

            binding.eventRatingNumericSeekBar.progress = (value / seekBarMultiplier).toInt()
        }
        binding.eventRatingNumericSeekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        binding.eventRatingNumeric.setText((progress * seekBarMultiplier).toString())
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            }
        )

        binding.eventNote.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val inputManager: InputMethodManager =
                    activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(
                    binding.eventNote.applicationWindowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
                binding.eventNote.clearFocus()
            }
            false
        }

        binding.buttonOk.setOnClickListener {
            if (quant is QuantBase.QuantMeasure) {
                if (binding.eventRatingNumeric.text.toString().isEmpty()) {
                    Toast.makeText(
                        context,
                        getString(R.string.create_event_not_enter_field),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@setOnClickListener
                }
            }

            dialogListener?.onConfirm(
                quant.toEvent(
                    existEvent?.id,
                    when (quant) {
                        is QuantBase.QuantRated -> binding.eventRating.rating.toDouble()
                        is QuantBase.QuantMeasure -> binding.eventRatingNumeric.text.toString()
                            .toDouble()

                        is QuantBase.QuantNote -> (-1).toDouble()
                    }, calendar.timeInMillis,
                    binding.eventNote.text.toString(),
                    isHidden
                ), quant.name
            )
            dismiss()
        }

        binding.buttonDelete.setOnClickListener {
            if (existEvent != null) {
                dialogListener?.onDelete(existEvent, quant.name)
            }
            dismiss()
        }

        binding.buttonBack.setOnClickListener {
            dismiss()
        }

        existEvent?.let {
            binding.buttonDelete.visibility = View.VISIBLE
            binding.eventNote.setText(it.note)
            binding.eventDate.text = it.date.toDate()
            isHidden = it.isHidden
            updateIsHiddenIcon(it.isHidden)

            calendar.timeInMillis = it.date
            when (it) {
                is EventBase.EventRated -> {
                    binding.eventRating.rating = it.rate.toFloat()
                }

                is EventBase.EventMeasure -> {
                    binding.eventRatingNumeric.setText(it.value.toString())
                    // TODO It wrong
                    binding.eventRatingNumericSeekBar.progress =
                        (it.value * SEEKBAR_MULTIPLIER).toInt()
                }

                else -> {}
            }
        }

        return binding.root
    }

    private fun onDateTimeSelected(newCalendar: Calendar) {
        calendar.timeInMillis = newCalendar.timeInMillis
        settingsInteractor.lastSelectedCalendar = calendar
        binding.eventDate.text = calendar.timeInMillis.toDate()
    }

    fun setDialogListener(listener: DialogListener) {
        dialogListener = listener
    }

    private fun updateIsHiddenIcon(isHidden: Boolean) {
        binding.eventHiddenButton.setImageResource(
            when (isHidden) {
                true -> R.drawable.quant_hidden
                false -> R.drawable.quant_show
            }
        )
    }

    interface DialogListener {
        fun onConfirm(event: EventBase, name: String)
        fun onDecline()
        fun onDelete(event: EventBase, name: String)
    }
}
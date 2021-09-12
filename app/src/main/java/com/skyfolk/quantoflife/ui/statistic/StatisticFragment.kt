package com.skyfolk.quantoflife.ui.statistic

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.skyfolk.quantoflife.R
import com.skyfolk.quantoflife.databinding.StatisticFragmentBinding
import com.skyfolk.quantoflife.meansure.QuantFilter
import com.skyfolk.quantoflife.meansure.fromPositionToMeasure
import com.skyfolk.quantoflife.ui.theme.Colors
import com.skyfolk.quantoflife.ui.theme.toInt
import com.skyfolk.quantoflife.utils.fromPositionToTimeInterval
import com.skyfolk.quantoflife.utils.toDateWithoutHourAndMinutes
import org.koin.android.viewmodel.ext.android.viewModel

class StatisticFragment : Fragment() {
    private val viewModel: StatisticViewModel by viewModel()
    private lateinit var binding: StatisticFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = StatisticFragmentBinding.inflate(inflater, container, false)

        binding.chart.description.isEnabled = false
        binding.chart.setPinchZoom(false)
        binding.chart.setDrawGridBackground(false)

        //TODO If one data source
        binding.chart.legend.isEnabled = true
        binding.chart.legend.textColor = Color.rgb(255, 255, 255)

        viewModel.barEntryData.observe(viewLifecycleOwner, { data ->
            when (data) {
                is StatisticFragmentState.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.chart.visibility = View.INVISIBLE
                    binding.chartNotEnought.visibility = View.INVISIBLE
                }
                is StatisticFragmentState.Entries -> {
                    if (data.entries.size > 0 && data.entries.first().entries.size > 1) {
                        binding.maximimumWithText.text = getString(R.string.statistic_maximum_with,
                            binding.timePeriodSpinner.selectedItem.toString(),
                            data.entries[0].name,
                            data.entries[0].maximumWith.lenght,
                            data.entries[0].maximumWith.startDate.toDateWithoutHourAndMinutes()
                        )

                        binding.maximimumWithoutText.text = getString(R.string.statistic_maximum_without,
                            binding.timePeriodSpinner.selectedItem.toString(),
                            data.entries[0].name,
                            data.entries[0].maximumWithout.lenght,
                            data.entries[0].maximumWithout.startDate.toDateWithoutHourAndMinutes()
                        )

                        val dataSets = arrayListOf<LineDataSet>()
                        val set1 = LineDataSet(data.entries[0].entries, data.entries[0].name)

                        setDefaultDataSetPropertiesForFirstSet(set1)

                        dataSets.add(set1)

                        if (data.entries.size > 1) {
                            val set2 = LineDataSet(data.entries[1].entries, data.entries[1].name)
                            setDefaultDataSetPropertiesForSecondSet(set2)
                            dataSets.add(set2)
                        }

                        setAxisProperties()

                        //TODO Даже в три раза меньше делений это может быть много, сделай нормально
                        binding.chart.xAxis.granularity = if (data.entries[0].entries.size > 20) {
                            (data.entries[0].entries[1].x - data.entries[0].entries[0].x) * 4
                        } else {
                            (data.entries[0].entries[1].x - data.entries[0].entries[0].x)
                        }
                        binding.chart.xAxis.labelCount = data.entries[0].entries.size

                        binding.timePeriodSpinner.selectedItemPosition.fromPositionToTimeInterval()
                            .let {
                                val xAxisFormatter =
                                    viewModel.getFormatter(data.entries[0].firstDate, it)
                                binding.chart.xAxis.valueFormatter = xAxisFormatter
                            }

                        val dataForGraph = LineData(dataSets.toList())
                        binding.chart.invalidate()
                        binding.chart.data = dataForGraph

                        binding.progress.visibility = View.INVISIBLE
                        binding.chart.visibility = View.VISIBLE
                        binding.chartNotEnought.visibility = View.INVISIBLE
                    } else {
                        // Not enough data
                        binding.progress.visibility = View.INVISIBLE
                        binding.chart.visibility = View.GONE
                        binding.chartNotEnought.visibility = View.VISIBLE
                    }
                }
            }
        })

        viewModel.selectedFilter.observe(viewLifecycleOwner, { filter: SelectedGraphFilter? ->
            filter?.let { it: SelectedGraphFilter ->
                val listOfQuantName = it.listOfQuants.map { it.name }.toMutableList()
                listOfQuantName.add(0, "Ничего")
                listOfQuantName.add(0, "Все события")
                val quantsSpinnerAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    listOfQuantName
                )
                quantsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.eventSpinner.adapter = quantsSpinnerAdapter
                binding.eventSpinner2.adapter = quantsSpinnerAdapter

                binding.meansureSpinner.setSelection(it.measure.toPosition(), false)
                binding.timePeriodSpinner.setSelection(it.timeInterval.toGraphPosition(), false)
                binding.eventSpinner.setSelection(it.filter.toGraphPosition(listOfQuantName), false)
                binding.eventSpinner2.setSelection(
                    it.filter2.toGraphPosition(listOfQuantName),
                    false
                )

                viewModel.runSearch()
            }
        })

        setListeners()

        return binding.root
    }

    private var isSelectionFromTouch = false

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        binding.eventSpinner.setOnTouchListener { _, _ ->
            this.isSelectionFromTouch = true
            false
        }

        binding.eventSpinner2.setOnTouchListener { _, _ ->
            this.isSelectionFromTouch = true
            false
        }

        binding.timePeriodSpinner.setOnTouchListener { _, _ ->
            this.isSelectionFromTouch = true
            false
        }

        binding.meansureSpinner.setOnTouchListener { _, _ ->
            this.isSelectionFromTouch = true
            false
        }

        binding.eventSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (!isSelectionFromTouch) {
                        return
                    }
                    val newSelectedEventFilterName = when (position) {
                        0 -> QuantFilter.All
                        1 -> QuantFilter.Nothing
                        else -> QuantFilter.OnlySelected(
                            parent.getItemAtPosition(position).toString()
                        )
                    }
                    viewModel.setEventFilter(1, filter = newSelectedEventFilterName)
                    isSelectionFromTouch = false
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        binding.eventSpinner2.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (!isSelectionFromTouch) {
                        return
                    }
                    val newSelectedEventFilterName = when (position) {
                        0 -> QuantFilter.All
                        1 -> QuantFilter.Nothing
                        else -> QuantFilter.OnlySelected(
                            parent.getItemAtPosition(position).toString()
                        )
                    }
                    viewModel.setEventFilter(2, filter = newSelectedEventFilterName)
                    isSelectionFromTouch = false
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        binding.meansureSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (!isSelectionFromTouch) {
                        return
                    }
                    val measure = position.fromPositionToMeasure()
                    viewModel.setMeasureFilter(measure = measure)
                    isSelectionFromTouch = false
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        binding.timePeriodSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (!isSelectionFromTouch) {
                        return
                    }
                    val timeInterval = position.fromPositionToTimeInterval()
                    viewModel.setTimeIntervalFilter(timeInterval = timeInterval)
                    isSelectionFromTouch = false
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
    }

    private fun setAxisProperties() {
        binding.chart.xAxis.position = XAxisPosition.BOTTOM
        binding.chart.xAxis.setDrawGridLines(true)
        binding.chart.xAxis.gridColor = Colors.White.toInt()
        binding.chart.xAxis.labelRotationAngle = -60F
        binding.chart.xAxis.textColor = Colors.White.toInt()
        binding.chart.axisLeft.textColor = Colors.White.toInt()
        binding.chart.axisRight.textColor = Colors.White.toInt()
    }

    private fun setDataSetProperties(
        set: LineDataSet,
        lineColor: Int,
        circleColor: Int,
        textSize: Float,
        textColor: Int,
        fillDrawable: Int
    ) {
        set.color = lineColor
        set.setDrawFilled(true)
        set.setDrawIcons(true)
        set.valueTextSize = textSize
        set.setCircleColor(circleColor)
        set.setValueTextColors(listOf(textColor))
        set.fillDrawable = ContextCompat.getDrawable(requireContext(), fillDrawable)
    }

    private fun setDefaultDataSetPropertiesForFirstSet(set: LineDataSet) {
        setDataSetProperties(
            set = set,
            lineColor = Colors.Red.toInt(),
            circleColor = Colors.Red.toInt(),
            textSize = 10f,
            textColor = Colors.Red.toInt(),
            fillDrawable = R.drawable.fade_red
        )
    }

    private fun setDefaultDataSetPropertiesForSecondSet(set: LineDataSet) {
        setDataSetProperties(
            set = set,
            lineColor = Colors.Green.toInt(),
            circleColor = Colors.Green.toInt(),
            textSize = 10f,
            textColor = Colors.Green.toInt(),
            fillDrawable = R.drawable.fade_green
        )
    }
}
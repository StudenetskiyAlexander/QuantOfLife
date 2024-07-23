package com.skyfolk.quantoflife.ui.statistic

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.skyfolk.quantoflife.GraphSelectedMode
import com.skyfolk.quantoflife.R
import com.skyfolk.quantoflife.databinding.StatisticFragmentBinding
import com.skyfolk.quantoflife.meansure.fromPositionToMeasure
import com.skyfolk.quantoflife.ui.adapter.GraphSelectedYearModeAdapter
import com.skyfolk.quantoflife.ui.adapter.QuantFilterModeAdapter
import com.skyfolk.quantoflife.ui.entity.QuantFilterMode
import com.skyfolk.quantoflife.ui.entity.GraphSelectedYearMode
import com.skyfolk.quantoflife.ui.theme.Colors
import com.skyfolk.quantoflife.ui.theme.toInt
import com.skyfolk.quantoflife.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class StatisticFragment : Fragment(), OnChartValueSelectedListener {

    companion object {

        const val FIRST_LINE_COLOR = Color.RED
        const val SECOND_LINE_COLOR = Color.GREEN
    }

    private val viewModel: StatisticViewModel by viewModel()
    private lateinit var binding: StatisticFragmentBinding

    private fun openFeedsFragment(event: NavigateToFeedEvent) {
        val bundle = bundleOf(
            NavigateToFeedEvent.START_DATE_KEY to event.startDate,
            NavigateToFeedEvent.END_DATE_KEY to event.endDate
        )
        this.findNavController().navigate(
            resId = R.id.action_global_to_feeds,
            args = bundle,
            navOptions = navOptions {
                launchSingleTop = true
            })
    }

    private fun Fragment.findNavController(): NavController =
        NavHostFragment.findNavController(this)

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

        viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            openFeedsFragment(event)
        }

        viewModel.barEntryData.observe(viewLifecycleOwner) { data ->
            when (data) {
                is StatisticFragmentState.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.chart.visibility = View.INVISIBLE
                    binding.chartNotEnought.visibility = View.INVISIBLE
                    binding.maximimumWithText.visibility = View.INVISIBLE
                    binding.maximimumWithoutText.visibility = View.INVISIBLE
                    binding.averageText.visibility = View.INVISIBLE
                }

                is StatisticFragmentState.Entries -> {
                    if (data.entries.size > 0 && data.entries.first().entries.size > 1) {
                        binding.averageText.text = data.entries.map {
                            getString(
                                R.string.statistic_average,
                                it.average.toString(),
                                binding.timePeriodSpinner.selectedItem.toString()
                            )
                        }.joinToString("\n")

                        binding.maximimumWithText.text = getString(
                            R.string.statistic_maximum_with,
                            binding.timePeriodSpinner.selectedItem.toString(),
                            data.entries[0].name,
                            data.entries[0].maximumWith.lenght,
                            data.entries[0].maximumWith.startDate.toDateWithoutHourAndMinutes()
                        )

                        binding.maximimumWithoutText.text =
                            when (data.entries[0].maximumWithout.lenght > 0) {
                                true -> {
                                    getString(
                                        R.string.statistic_maximum_without,
                                        binding.timePeriodSpinner.selectedItem.toString(),
                                        data.entries[0].name,
                                        data.entries[0].maximumWithout.lenght,
                                        data.entries[0].maximumWithout.startDate.toDateWithoutHourAndMinutes()
                                    )
                                }

                                false -> {
                                    getString(
                                        R.string.statistic_maximum_without_no_one,
                                        binding.timePeriodSpinner.selectedItem.toString(),
                                        data.entries[0].name
                                    )
                                }
                            }

                        val dataSets = arrayListOf<LineDataSet>()
                        val set1 =
                            LineDataSet(data.entries[0].entries.map { it }, data.entries[0].name)

                        setDefaultDataSetPropertiesForFirstSet(set1)

                        dataSets.add(set1)

                        if (data.entries.size > 1) {
                            val set2 = LineDataSet(
                                data.entries[1].entries.map { it },
                                data.entries[1].name
                            )
                            setDefaultDataSetPropertiesForSecondSet(set2)
                            dataSets.add(set2)
                        }

                        setAxisProperties()

                        //TODO Даже в три раза меньше делений это может быть много, сделай нормально
                        binding.chart.xAxis.granularity =
                            if (data.entries[0].entries.size > 20) {
                                (data.entries[0].entries[1].x - data.entries[0].entries[0].x) * 4
                            } else {
                                (data.entries[0].entries[1].x - data.entries[0].entries[0].x)
                            }
                        binding.chart.xAxis.labelCount = data.entries[0].entries.size

                        binding.chart.setOnChartValueSelectedListener(this)

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
                        binding.maximimumWithText.visibility = View.VISIBLE
                        binding.maximimumWithoutText.visibility = View.VISIBLE
                        binding.averageText.visibility = View.VISIBLE
                    } else {
                        // Not enough data
                        binding.progress.visibility = View.INVISIBLE
                        binding.chart.visibility = View.INVISIBLE
                        binding.chartNotEnought.visibility = View.VISIBLE
                        binding.maximimumWithText.visibility = View.INVISIBLE
                        binding.maximimumWithoutText.visibility = View.INVISIBLE
                        binding.averageText.visibility = View.INVISIBLE
                    }
                }
            }
        }

        viewModel.selectedFilter.observe(viewLifecycleOwner) { filter: SelectedGraphFilter? ->
            filter?.let { it: SelectedGraphFilter ->
                val listOfQuantFilterModes: MutableList<QuantFilterMode> =
                    it.listOfQuants.map { QuantFilterMode.OnlySelected(it) }.toMutableList()
                listOfQuantFilterModes.add(0, QuantFilterMode.All)
                val quantsSpinnerAdapter = QuantFilterModeAdapter(
                    requireContext(),
                    listOfQuantFilterModes
                )
                binding.eventSpinner.adapter = quantsSpinnerAdapter
                binding.eventSpinner2.adapter = quantsSpinnerAdapter

                binding.modeSpinner.setSelection(it.selectedMode.toPosition(), false)
                binding.meansureSpinner.setSelection(it.measure.toPosition(), false)
                quantsSpinnerAdapter.getPosition(it.filter)
                binding.timePeriodSpinner.setSelection(it.timeInterval.toGraphPosition(), false)

                binding.eventSpinner.setSelection(
                    quantsSpinnerAdapter.getPosition(it.filter),
                    false
                )
                binding.eventSpinner2.setSelection(
                    quantsSpinnerAdapter.getPosition(it.filter2),
                    false
                )

                val listOfYears: MutableList<GraphSelectedYearMode> = it.listOfYears.map {
                    GraphSelectedYearMode.OnlyYearMode(it)
                }.toMutableList()
                listOfYears.add(0, GraphSelectedYearMode.All)
                val yearsSpinnerAdapter = GraphSelectedYearModeAdapter(
                    requireContext(),
                    listOfYears
                )
                binding.yearPeriodSpinner.adapter = yearsSpinnerAdapter
                binding.yearPeriodSpinner.setSelection(
                    yearsSpinnerAdapter.getPosition(it.selectedYear),
                    false
                )

                binding.yearPeriodSpinner2.adapter = yearsSpinnerAdapter
                binding.yearPeriodSpinner2.setSelection(
                    yearsSpinnerAdapter.getPosition(it.selectedYear2),
                    false
                )

                with(binding) {
                    when (it.selectedMode) {
                        GraphSelectedMode.Common -> {
                            yearPeriodSpinner2.visibility = View.GONE
                            yearPeriodSpinnerColor.visibility = View.GONE
                            yearPeriodSpinnerColor2.visibility = View.GONE
                            eventSpinner2.visibility = View.GONE
                            eventSpinnerColor.visibility = View.GONE
                            eventSpinnerColor2.visibility = View.GONE
                        }

                        GraphSelectedMode.Comparison -> {
                            yearPeriodSpinner2.visibility = View.VISIBLE
                            yearPeriodSpinnerColor.visibility = View.VISIBLE
                            yearPeriodSpinnerColor2.visibility = View.VISIBLE
                            eventSpinner2.visibility = View.GONE
                            eventSpinnerColor.visibility = View.GONE
                            eventSpinnerColor2.visibility = View.GONE
                        }

                        GraphSelectedMode.Dependence -> {
                            yearPeriodSpinner2.visibility = View.GONE
                            yearPeriodSpinnerColor.visibility = View.GONE
                            yearPeriodSpinnerColor2.visibility = View.GONE
                            eventSpinner2.visibility = View.VISIBLE
                            eventSpinnerColor.visibility = View.VISIBLE
                            eventSpinnerColor2.visibility = View.VISIBLE
                        }
                    }
                }

                viewModel.runSearch()
            }
        }

        setListeners()

        return binding.root
    }

    private var isSelectionFromTouch = false

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        binding.modeSpinner.setOnTouchListener { _, _ ->
            this.isSelectionFromTouch = true
            false
        }

        binding.yearPeriodSpinner.setOnTouchListener { _, _ ->
            this.isSelectionFromTouch = true
            false
        }

        binding.yearPeriodSpinner2.setOnTouchListener { _, _ ->
            this.isSelectionFromTouch = true
            false
        }

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

        binding.modeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (!isSelectionFromTouch) return
                    val newSelectedMode = GraphSelectedMode.values().find {
                        position == it.ordinal
                    }
                    viewModel.setGraphMode(newSelectedMode ?: GraphSelectedMode.Common)
                    isSelectionFromTouch = false
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        binding.yearPeriodSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (!isSelectionFromTouch) return
                    val newMode = parent.getItemAtPosition(position) as GraphSelectedYearMode
                    viewModel.setYearFilter(filter = newMode)
                    isSelectionFromTouch = false
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        binding.yearPeriodSpinner2.onItemSelectedListener =
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
                    val newSelectedYearName =
                        parent.getItemAtPosition(position) as GraphSelectedYearMode
                    viewModel.setYearFilter2(filter = newSelectedYearName)
                    isSelectionFromTouch = false
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        binding.eventSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (!isSelectionFromTouch) return
                    val newMode = parent.getItemAtPosition(position) as QuantFilterMode
                    viewModel.setEventFilter(1, filter = newMode)
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
                    val newMode = parent.getItemAtPosition(position) as QuantFilterMode
                    viewModel.setEventFilter(2, filter = newMode)
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
        set: BarDataSet,
        lineColor: Int,
        circleColor: Int,
        textSize: Float,
        textColor: Int,
        fillDrawable: Int
    ) {
        set.color = lineColor
        set.setDrawIcons(true)
        set.valueTextSize = textSize
        set.setValueTextColors(listOf(textColor))
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
        set.lineWidth = 4F
        set.setDrawIcons(true)
        set.valueTextSize = textSize
        set.setValueTextColors(listOf(textColor))
    }

    private fun setDefaultDataSetPropertiesForFirstSet(set: BarDataSet) {
        setDataSetProperties(
            set = set,
            lineColor = FIRST_LINE_COLOR,
            circleColor = FIRST_LINE_COLOR,
            textSize = 10f,
            textColor = FIRST_LINE_COLOR,
            fillDrawable = R.drawable.fade_red
        )
    }

    private fun setDefaultDataSetPropertiesForFirstSet(set: LineDataSet) {
        setDataSetProperties(
            set = set,
            lineColor = FIRST_LINE_COLOR,
            circleColor = FIRST_LINE_COLOR,
            textSize = 10f,
            textColor = FIRST_LINE_COLOR,
            fillDrawable = R.drawable.fade_red
        )
    }

    private fun setDefaultDataSetPropertiesForSecondSet(set: BarDataSet) {
        setDataSetProperties(
            set = set,
            lineColor = SECOND_LINE_COLOR,
            circleColor = SECOND_LINE_COLOR,
            textSize = 10f,
            textColor = SECOND_LINE_COLOR,
            fillDrawable = R.drawable.fade_green
        )
    }

    private fun setDefaultDataSetPropertiesForSecondSet(set: LineDataSet) {
        setDataSetProperties(
            set = set,
            lineColor = SECOND_LINE_COLOR,
            circleColor = SECOND_LINE_COLOR,
            textSize = 10f,
            textColor = SECOND_LINE_COLOR,
            fillDrawable = R.drawable.fade_green
        )
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        h?.let {
            viewModel.selectGraphBar(it.x.toInt())
        }
    }

    override fun onNothingSelected() {
    }
}
package com.skyfolk.quantoflife.ui.now

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.skyfolk.quantoflife.R
import com.skyfolk.quantoflife.databinding.NowFragmentBinding
import com.skyfolk.quantoflife.entity.*
import com.skyfolk.quantoflife.settings.SettingsInteractor
import com.skyfolk.quantoflife.ui.goals.view.GoalView
import com.skyfolk.quantoflife.ui.now.CreateEventDialogFragment.DialogListener
import com.skyfolk.quantoflife.ui.now.create.CreateEventComposeFragment
import com.skyfolk.quantoflife.utils.setOnHideByTimeout
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class NowFragment : Fragment() {
    private val viewModel: NowViewModel by viewModel()
    private val settingsInteractor: SettingsInteractor by inject()

    private lateinit var binding: NowFragmentBinding
    private var isAllFabVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NowFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.toastState.observe(viewLifecycleOwner) { message ->
            if (message != "") {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
        viewModel.dialogState.observe(viewLifecycleOwner) { dialog ->
            if (dialog != null) {
                val fm: FragmentManager = requireActivity().supportFragmentManager
                dialog.show(fm, dialog.tag)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.todayTotal.collect {
                binding.todayRating.text = String.format("%.1f", it)
                val todayScore = resources.getStringArray(R.array.today_score)
                binding.todayRatingScore.text = when (it) {
                    in -Double.MAX_VALUE..2.0 -> todayScore[0]
                    in 2.0..4.0 -> todayScore[1]
                    in 4.0..6.0 -> todayScore[2]
                    in 6.0..8.0 -> todayScore[3]
                    in 8.0..Double.MAX_VALUE -> todayScore[4]
                    else -> ""
                }
            }
        }

        viewModel.listOfGoal.observe(viewLifecycleOwner) { goals ->
            if (goals.isEmpty()) binding.goalsComposeView.visibility = View.GONE
            else {
                binding.goalsComposeView.visibility = View.VISIBLE
                binding.goalsComposeView.setContent {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        goals.forEach { goal ->
                            GoalView(goal = goal) {
                                viewModel.openCreateNewGoalDialog(goal.id)
                            }
                        }
                    }
                }
            }
        }

        binding.categoryPhysical.text = settingsInteractor.categoryNames[QuantCategory.Physical]
        binding.categoryEmotion.text = settingsInteractor.categoryNames[QuantCategory.Emotion]
        binding.categoryEvolution.text = settingsInteractor.categoryNames[QuantCategory.Evolution]
        binding.categoryOther.text = settingsInteractor.categoryNames[QuantCategory.Other]

        val listOfQuants: RecyclerView = binding.listOfPhysicalQuants
        val listOfEmotionQuants: RecyclerView = binding.listOfEmotionQuants
        val listOfEvolutionQuants: RecyclerView = binding.listOfEvolutionQuants
        val listOfOtherQuants: RecyclerView = binding.listOfOtherQuants
        listOfQuants.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        listOfEmotionQuants.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        listOfEvolutionQuants.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        listOfOtherQuants.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)

        val quantListClickListener: (quant: QuantBase) -> Unit = {
            val listener = object : DialogListener {
                override fun onConfirm(event: EventBase, name: String) {
                    val snackBar = Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        getString(R.string.now_event_created, name),
                        Snackbar.LENGTH_LONG
                    )
                    snackBar.setAction(getString(R.string.cancel)) {
                    }
                    snackBar.setOnHideByTimeout {
                        viewModel.onEventCreated(event)
                    }
                    snackBar.show()
                }

                override fun onDecline() {
                }

                override fun onDelete(event: EventBase, name: String) {
                    //Событие не может быть удалено при его создании
                }
            }

            val useComposeDialog = false
            val dialog = when (useComposeDialog) {
                true -> CreateEventComposeFragment(it, listener)
                false -> CreateEventDialogFragment(it).also { it.setDialogListener(listener) }
            }

            val fm: FragmentManager = requireActivity().supportFragmentManager
            dialog.show(fm, dialog.tag)
        }

        val quantListLongClickListener: (quant: QuantBase) -> Boolean = {
            viewModel.openCreateNewQuantDialog(it)
            true
        }

        lifecycleScope.launch {
            viewModel.listOfQuants.observe(viewLifecycleOwner) { quantsList ->
                val adapterPhysical =
                    QuantListDataAdapter(quantsList.filter { it.primalCategory == QuantCategory.Physical },
                        { quant -> quantListClickListener(quant) },
                        { quant -> quantListLongClickListener(quant) })

                val adapterEmotion =
                    QuantListDataAdapter(quantsList.filter { it.primalCategory == QuantCategory.Emotion },
                        { quant -> quantListClickListener(quant) },
                        { quant -> quantListLongClickListener(quant) })

                val adapterEvolution =
                    QuantListDataAdapter(quantsList.filter { it.primalCategory == QuantCategory.Evolution },
                        { quant -> quantListClickListener(quant) },
                        { quant -> quantListLongClickListener(quant) })

                val adapterOther =
                    QuantListDataAdapter(quantsList.filter { it.primalCategory == QuantCategory.Other },
                        { quant -> quantListClickListener(quant) },
                        { quant -> quantListLongClickListener(quant) })

                listOfQuants.adapter = adapterPhysical
                listOfEmotionQuants.adapter = adapterEmotion
                listOfEvolutionQuants.adapter = adapterEvolution
                listOfOtherQuants.adapter = adapterOther
            }
        }

        binding.fab.shrink()
        binding.fab.setOnClickListener {
            when (isAllFabVisible) {
                true -> {
                    binding.addGoalFab.hide()
                    binding.addQuantFab.hide()
                    binding.fab.shrink()
                    binding.addFoalFabText.visibility = View.GONE
                    binding.addQuantFabText.visibility = View.GONE
                }

                false -> {
                    binding.addGoalFab.show()
                    binding.addQuantFab.show()
                    binding.fab.extend()
                    binding.addFoalFabText.visibility = View.VISIBLE
                    binding.addQuantFabText.visibility = View.VISIBLE
                }
            }
            isAllFabVisible = isAllFabVisible.not()
        }

        binding.addQuantFab.setOnClickListener {
            viewModel.openCreateNewQuantDialog(null)
        }

        binding.addGoalFab.setOnClickListener {
            viewModel.openCreateNewGoalDialog(null)
        }
    }
}

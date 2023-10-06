package com.skyfolk.quantoflife.ui.goals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.skyfolk.quantoflife.R
import com.skyfolk.quantoflife.entity.GoalPresentation

class GoalsListDataAdapter(
    private val goalsList: List<GoalPresentation>,
    private val longClickListener: (GoalPresentation) -> Boolean
) : RecyclerView.Adapter<GoalsListDataAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.goal_present, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(goalsList[position], longClickListener)
    }

    override fun getItemCount(): Int {
        return goalsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            goal: GoalPresentation,
            deleteClickListener: (GoalPresentation) -> Boolean
        ) {
            val progressBar: LinearProgressIndicator = itemView.findViewById(R.id.goal_progress)
            val progressText: TextView = itemView.findViewById(R.id.goal_progress_text)
            val moreText: TextView = itemView.findViewById(R.id.goal_more)
            val titleText: TextView = itemView.findViewById(R.id.goal_title)

            titleText.text = goal.targetText
            progressBar.setProgress(goal.progress, false)
            progressText.text = goal.progressText
            progressBar.setIndicatorColor(goal.barColor)
            moreText.text = goal.additionText

            itemView.setOnLongClickListener {
                deleteClickListener(goal)
                true
            }
        }
    }
}
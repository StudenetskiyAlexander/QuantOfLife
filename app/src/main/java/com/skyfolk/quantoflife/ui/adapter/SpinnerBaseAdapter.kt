package com.skyfolk.quantoflife.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.skyfolk.quantoflife.R

abstract class SpinnerBaseAdapter<T : SpinnerSelectableItem>(
    context: Context,
    listOfModes: List<T>
) : ArrayAdapter<T>(
    context,
    R.layout.li_statistic_spinner_item,
    listOfModes
) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val mode = getItem(position)

        val view = when (convertView == null) {
            true -> LayoutInflater.from(context).inflate(R.layout.li_statistic_spinner_item, null)
            false -> convertView
        }
        (view.findViewById(R.id.spinner_text) as TextView).text = mode?.toPresentation()

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val mode = getItem(position)
        val view = when (convertView == null) {
            true -> LayoutInflater.from(context)
                .inflate(R.layout.li_statistic_spinner_dropdown_item, null)
            false -> convertView
        }
        (view.findViewById(R.id.spinner_text) as TextView).text = mode?.toPresentation()

        return view
    }
}
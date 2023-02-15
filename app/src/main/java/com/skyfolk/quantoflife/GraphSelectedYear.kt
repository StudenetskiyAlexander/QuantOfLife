package com.skyfolk.quantoflife

sealed class GraphSelectedYear {
    object All: GraphSelectedYear()
    data class OnlyYear(val year: Int): GraphSelectedYear()

    fun toGraphPosition(listOfYears: List<String>) : Int {
        return when (this) {
            is All -> 0
            is OnlyYear -> {
                listOfYears.indexOf(year.toString())
            }
        }
    }
}
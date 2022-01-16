package com.skyfolk.quantoflife

enum class GraphSelectedMode {
    Common, Dependence, Comparison;

    fun toPosition(): Int {
        return this.ordinal
    }
}


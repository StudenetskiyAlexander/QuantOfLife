package com.skyfolk.quantoflife.ui.now.date_picker

enum class Days(val abbreviation: String, val value: String, val number: Int, val isRedDay: Boolean) {
    MONDAY("ПН", "Monday", 1, false),
    TUESDAY("ВТ", "Tuesday", 2, false),
    WEDNESDAY("СР", "Wednesday", 3, false),
    THURSDAY("ЧТ", "Thursday", 4, false),
    FRIDAY("ПТ", "Friday", 5, false),
    SATURDAY("СБ", "Saturday", 6, true),
    SUNDAY("ВС", "Sunday", 7, true);

    companion object {
        fun get(number: Int): Days {
            return when (number) {
                1 -> SUNDAY
                2 -> MONDAY
                3 -> TUESDAY
                4 -> WEDNESDAY
                5 -> THURSDAY
                6 -> FRIDAY
                else -> SATURDAY
            }
        }
    }
}
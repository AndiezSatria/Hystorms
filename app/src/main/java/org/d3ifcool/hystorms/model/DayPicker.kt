package org.d3ifcool.hystorms.model

data class DayPicker(
    val id: Int,
    val day: String = when (id) {
        0 -> "Minggu"
        1 -> "Senin"
        2 -> "Selasa"
        3 -> "Rabu"
        4 -> "Kamis"
        5 -> "Jumat"
        6 -> "Sabtu"
        else -> "Tidak ada"
    }
)

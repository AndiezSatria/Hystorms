package org.d3ifcool.hystorms.util

import java.text.SimpleDateFormat
import java.util.*

class CalendarMapper {
    companion object {
        fun getStringFormatTime(
            calendar: Calendar = Calendar.getInstance(Locale("id", "ID")),
            format: String
        ): String {
            val formatter = SimpleDateFormat(format, Locale("id", "ID"))
            return formatter.format(calendar.time)
        }
    }
}
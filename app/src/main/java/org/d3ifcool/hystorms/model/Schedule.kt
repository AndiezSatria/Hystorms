package org.d3ifcool.hystorms.model

import java.util.*

data class Schedule(
    var id: String = "",
    var title: String = "",
    var nutrition: String = "",
    var owner: String = "",
    var tank: String = "",
    var time: Date = Calendar.getInstance().time,
    @field:JvmField  var isDaily: Boolean = true
)

package org.d3ifcool.hystorms.model

import java.io.Serializable
import java.util.*

data class Schedule(
    var id: Long = 0,
    var title: String = "",
    var owner: String = "",
    var tank: String = "",
    var day: List<Int> = listOf(),
    var time: Date = Calendar.getInstance().time
) : Serializable

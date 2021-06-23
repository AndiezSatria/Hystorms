package org.d3ifcool.hystorms.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class History(
    var id: String = "",
    var data: List<DataSensor> = listOf(),
    var tank: String = "",
    @ServerTimestamp var timestamp: Date = Calendar.getInstance().time
)
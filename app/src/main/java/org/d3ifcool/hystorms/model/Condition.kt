package org.d3ifcool.hystorms.model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class Condition(
    var id: String = "",
    @ServerTimestamp var timeStamp: Date = Calendar.getInstance().time,
    var data: List<SensorPhysic> = listOf()
) : Serializable

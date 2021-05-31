package org.d3ifcool.hystorms.model

import java.io.Serializable

data class DataSensor(
    var id: Int = 1,
    var name: String = "",
    var data: Double? = 0.0,
    @field:JvmField var isLowerFromOptimum: Boolean = false,
    @field:JvmField var isHigherFromOptimum: Boolean = false
) : Serializable
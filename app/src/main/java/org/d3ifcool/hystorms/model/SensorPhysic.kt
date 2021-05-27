package org.d3ifcool.hystorms.model

import java.io.Serializable

data class SensorPhysic(
    var id: Int = 1,
    @field:JvmField var isError: Boolean = false,
    var name: String = "",
) : Serializable

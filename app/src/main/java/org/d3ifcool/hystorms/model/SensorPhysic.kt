package org.d3ifcool.hystorms.model

import java.io.Serializable

data class SensorPhysic(
    val id: Int = 1,
    val name: String = "",
    val isError: Boolean = false
): Serializable

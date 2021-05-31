package org.d3ifcool.hystorms.model

import java.io.Serializable

data class OptimumData(
    var id: Int = 0,
    var name: String = "",
    var min: Double = 0.0,
    var max: Double = 0.0
): Serializable

package org.d3ifcool.hystorms.model

import java.io.Serializable

data class Nutrition(
    var id: String = "",
    var name: String = "",
    var nutrientContent: String = "",
    var usage: String = "",
    var photoUrl: String? = null,
    var ppm: Int? = 0,
    var owner: String? = null,
    var effect: String = "",
    var description: String = ""
) : Serializable
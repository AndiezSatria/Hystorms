package org.d3ifcool.hystorms.model

data class Nutrition(
    var id: String = "",
    var name: String = "",
    var ingredient: String = "",
    var photoUrl: String? = "",
    var ppm: Int? = 0,
    var owner: String? = "",
    var effect: String = "",
    var description: String = ""
)
package org.d3ifcool.hystorms.model

import java.io.Serializable

data class Plant(
    var id: String = "",
    var name: String = "",
    var photoUrl: String? = "",
    var scientificName: String = "",
    var optimumData: List<OptimumData> = listOf()
) : Serializable

package org.d3ifcool.hystorms.model

data class DataOrException<T, E : Exception>(
    var data: T? = null,
    var exception: E? = null
)
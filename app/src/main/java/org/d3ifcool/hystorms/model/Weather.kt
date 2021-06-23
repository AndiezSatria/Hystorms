package org.d3ifcool.hystorms.model

import java.io.Serializable

data class Weather(
    val id: Int,
    val idIcon: Int,
    val main: String,
    val cityName: String,
    val description: String,
    val icon: String,
    val temp: Long,
    val feelsLike: Long,
    val pressure: Long,
    val humidity: Long,
    val windSpeed: Long,
    val windDegree: Long
) : Serializable

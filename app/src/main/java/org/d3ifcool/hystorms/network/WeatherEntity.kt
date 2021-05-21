package org.d3ifcool.hystorms.network

import kotlinx.parcelize.RawValue

data class WeatherEntity(
    val weather: @RawValue List<Weathers>,
    val main: @RawValue MainDataWeather,
    val wind: @RawValue Wind
)

data class Weathers(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class MainDataWeather(
    val temp: Float,
    val feels_like: Float,
    val pressure: Int,
    val humidity: Int
)

data class Wind(
    val speed: Int,
    val deg: Int
)

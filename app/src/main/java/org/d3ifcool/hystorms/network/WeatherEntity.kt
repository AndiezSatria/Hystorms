package org.d3ifcool.hystorms.network

data class WeatherEntity(
    val weather: List<Weathers>,
    val main: MainDataWeather,
    val wind: Wind,
    val name: String
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
    val speed: Float,
    val deg: Int
)

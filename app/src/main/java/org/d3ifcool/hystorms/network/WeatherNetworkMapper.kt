package org.d3ifcool.hystorms.network

import org.d3ifcool.hystorms.model.Weather
import org.d3ifcool.hystorms.util.EntityMapper
import javax.inject.Inject

class WeatherNetworkMapper @Inject constructor() : EntityMapper<WeatherEntity, Weather> {
    override fun mapFromEntity(entity: WeatherEntity): Weather {
        val weather = entity.weather[0]
        return Weather(
            id = weather.id,
            description = weather.description,
            icon = weather.icon,
            temp = entity.main.temp.toLong(),
            pressure = entity.main.pressure.toLong(),
            feelsLike = entity.main.feels_like.toLong(),
            humidity = entity.main.humidity.toLong(),
            windDegree = entity.wind.deg.toLong(),
            windSpeed = entity.wind.speed.toLong()
        )
    }

    override fun mapFromDomain(domain: Weather): WeatherEntity {
        val weathers: List<Weathers> = listOf(
            Weathers(
                id = domain.id,
                description = domain.description,
                main = domain.description,
                icon = domain.icon
            )
        )
        val main = MainDataWeather(
            temp = domain.temp.toFloat(),
            feels_like = domain.feelsLike.toFloat(),
            pressure = domain.pressure.toInt(),
            humidity = domain.humidity.toInt()
        )
        val wind = Wind(
            domain.windSpeed.toInt(),
            domain.windDegree.toInt()
        )
        return WeatherEntity(weathers, main, wind)
    }
}
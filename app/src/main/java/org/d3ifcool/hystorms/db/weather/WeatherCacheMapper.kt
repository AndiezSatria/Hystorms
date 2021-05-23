package org.d3ifcool.hystorms.db.weather

import org.d3ifcool.hystorms.model.Weather
import org.d3ifcool.hystorms.util.EntityMapper
import javax.inject.Inject

class WeatherCacheMapper @Inject constructor() : EntityMapper<WeatherCacheEntity, Weather> {
    override fun mapFromEntity(entity: WeatherCacheEntity): Weather {
        return Weather(
            entity.id,
            entity.idIcon,
            entity.main,
            entity.name,
            entity.description,
            entity.icon,
            entity.temp,
            entity.feelsLike,
            entity.pressure,
            entity.humidity,
            entity.windSpeed,
            entity.windDegree
        )
    }

    override fun mapFromDomain(domain: Weather): WeatherCacheEntity {
        return WeatherCacheEntity(
            1,
            domain.idIcon,
            domain.main,
            domain.cityName,
            domain.description,
            domain.icon,
            domain.temp,
            domain.feelsLike,
            domain.pressure,
            domain.humidity,
            domain.windSpeed,
            domain.windDegree
        )
    }

    fun mapFromEntitiesList(entities: List<WeatherCacheEntity>): List<Weather> {
        return entities.map {
            Weather(
                it.id,
                it.idIcon,
                it.main,
                it.name,
                it.description,
                it.icon,
                it.temp,
                it.feelsLike,
                it.pressure,
                it.humidity,
                it.windSpeed,
                it.windDegree
            )
        }
    }
}
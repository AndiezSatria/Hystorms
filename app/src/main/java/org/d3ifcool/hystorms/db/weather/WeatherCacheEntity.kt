package org.d3ifcool.hystorms.db.weather

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = WeatherCacheEntity.TABLE_NAME)
data class WeatherCacheEntity(
    @PrimaryKey val id: Int,
    val description: String,
    val icon: String,
    val temp: Long,
    val feelsLike: Long,
    val pressure: Long,
    val humidity: Long,
    val windSpeed: Long,
    val windDegree: Long
) {
    companion object {
        const val TABLE_NAME = "weather_cache"
    }
}
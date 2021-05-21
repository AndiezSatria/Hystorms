package org.d3ifcool.hystorms.db.weather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherEntity: WeatherCacheEntity): Long

    @Query("SELECT * FROM weather_cache")
    suspend fun get(): List<WeatherCacheEntity>

    @Query("SELECT * FROM weather_cache ORDER BY id DESC LIMIT 1")
    suspend fun getLatestCache(): WeatherCacheEntity
}

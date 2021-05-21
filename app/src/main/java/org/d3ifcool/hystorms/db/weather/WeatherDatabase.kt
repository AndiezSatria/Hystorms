package org.d3ifcool.hystorms.db.weather

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WeatherCacheEntity::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    companion object {
        const val DATABASE_NAME = "weather_db"
    }
}
package org.d3ifcool.hystorms.repository

import org.d3ifcool.hystorms.BuildConfig
import org.d3ifcool.hystorms.db.weather.WeatherCacheMapper
import org.d3ifcool.hystorms.db.weather.WeatherDao
import org.d3ifcool.hystorms.model.DataOrException
import org.d3ifcool.hystorms.model.Weather
import org.d3ifcool.hystorms.network.WeatherNetworkMapper
import org.d3ifcool.hystorms.network.WeatherRetrofit

class HomeRepository constructor(
    private val weatherService: WeatherRetrofit,
    private val weatherDao: WeatherDao,
    private val networkMapper: WeatherNetworkMapper,
    private val cacheMapper: WeatherCacheMapper
    // Firestore Tank Reference
    // Firestore Schedule Reference
) {
    suspend fun getWeather(
        lat: Long,
        long: Long,
        language: String,
    ) {
        val dataOrException: DataOrException<Weather, Exception> = DataOrException()
        try {
            val networkWeather = weatherService.get(lat, long, BuildConfig.APPLICATION_ID, language)
            val weather = networkMapper.mapFromEntity(networkWeather)
            weatherDao.insert(cacheMapper.mapFromDomain(weather))
            val cacheWeather = weatherDao.getLatestCache()
            dataOrException.data = cacheMapper.mapFromEntity(cacheWeather)
        } catch (e: Exception) {
            dataOrException.exception = e
        }
    }
}
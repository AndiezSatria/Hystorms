package org.d3ifcool.hystorms.repository

import androidx.lifecycle.MutableLiveData
import org.d3ifcool.hystorms.BuildConfig
import org.d3ifcool.hystorms.db.weather.WeatherCacheMapper
import org.d3ifcool.hystorms.db.weather.WeatherDao
import org.d3ifcool.hystorms.model.DataOrException
import org.d3ifcool.hystorms.model.Weather
import org.d3ifcool.hystorms.network.WeatherNetworkMapper
import org.d3ifcool.hystorms.network.WeatherRetrofit
import org.d3ifcool.hystorms.util.ViewState

class HomeRepository constructor(
    private val weatherService: WeatherRetrofit,
    private val weatherDao: WeatherDao,
    private val networkMapper: WeatherNetworkMapper,
    private val cacheMapper: WeatherCacheMapper
    // Firestore Tank Reference
    // Firestore Schedule Reference
) {
    val weatherMutableLiveData: MutableLiveData<DataOrException<Weather, Exception>> =
        MutableLiveData()

    val weatherViewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.NOTHING)

    private fun setWeatherState(state: ViewState) = state.also { weatherViewState.value = it }

    suspend fun getWeather(
        lat: Long,
        long: Long,
        language: String,
    ) {
        val dataOrException: DataOrException<Weather, Exception> = DataOrException()
        setWeatherState(ViewState.LOADING)
        try {
            val networkWeather = weatherService.get(lat, long, BuildConfig.APPLICATION_ID, language)
            val weather = networkMapper.mapFromEntity(networkWeather)
            weatherDao.insert(cacheMapper.mapFromDomain(weather))
            val cacheWeather = weatherDao.getLatestCache().also {
                setWeatherState(ViewState.SUCCESS)
            }
            dataOrException.data = cacheMapper.mapFromEntity(cacheWeather)
            weatherMutableLiveData.value = dataOrException
        } catch (e: Exception) {
            dataOrException.exception = e
            weatherMutableLiveData.value = dataOrException
            setWeatherState(ViewState.ERROR)
        }
    }
}
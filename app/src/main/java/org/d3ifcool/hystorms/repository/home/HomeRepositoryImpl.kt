package org.d3ifcool.hystorms.repository.home

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.d3ifcool.hystorms.BuildConfig
import org.d3ifcool.hystorms.db.weather.WeatherCacheMapper
import org.d3ifcool.hystorms.db.weather.WeatherDao
import org.d3ifcool.hystorms.model.DataOrException
import org.d3ifcool.hystorms.model.Weather
import org.d3ifcool.hystorms.network.WeatherNetworkMapper
import org.d3ifcool.hystorms.network.WeatherRetrofit
import org.d3ifcool.hystorms.state.DataState

class HomeRepositoryImpl constructor(
    private val weatherService: WeatherRetrofit,
    private val weatherDao: WeatherDao,
    private val networkMapper: WeatherNetworkMapper,
    private val cacheMapper: WeatherCacheMapper
    // Firestore Tank Reference
    // Firestore Schedule Reference
) : HomeRepository {
    val weatherMutableLiveData: MutableLiveData<DataOrException<Weather, Exception>> =
        MutableLiveData()

    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        lang: String
    ): Flow<DataState<Weather>> =
        flow {
            emit(DataState.loading())
            delay(1000)
            try {
                val networkWeather =
                    weatherService.get(lat, lon, BuildConfig.WEATHER_API_ID, lang)
                val weather = networkMapper.mapFromEntity(networkWeather)
                weatherDao.insert(cacheMapper.mapFromDomain(weather))
                val cacheWeather = weatherDao.getLatestCache()
                emit(DataState.success(cacheMapper.mapFromEntity(cacheWeather)))
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }

//    ovveride suspend fun getWeather(
//        lat: Double,
//        long: Double,
//        language: String,
//    ): Flow<DataState<Weather>> {
//        val dataOrException: DataOrException<Weather, Exception> = DataOrException()
//        setWeatherState(ViewState.LOADING)
//        try {
//            val id = BuildConfig.WEATHER_API_ID
//            Action.showLog(id)
//            val networkWeather = weatherService.get(lat, long, BuildConfig.WEATHER_API_ID, language)
//            val weather = networkMapper.mapFromEntity(networkWeather)
//            weatherDao.insert(cacheMapper.mapFromDomain(weather))
//            val cacheWeather = weatherDao.getLatestCache().also {
//                setWeatherState(ViewState.SUCCESS)
//            }
//            dataOrException.data = cacheMapper.mapFromEntity(cacheWeather)
//            weatherMutableLiveData.value = dataOrException
//        } catch (e: Exception) {
//            dataOrException.exception = e
//            weatherMutableLiveData.value = dataOrException
//            setWeatherState(ViewState.ERROR)
//        }
//    }
}
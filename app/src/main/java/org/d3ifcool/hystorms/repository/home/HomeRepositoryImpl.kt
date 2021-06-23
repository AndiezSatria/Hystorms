package org.d3ifcool.hystorms.repository.home

import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.d3ifcool.hystorms.BuildConfig
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.db.weather.WeatherCacheMapper
import org.d3ifcool.hystorms.db.weather.WeatherDao
import org.d3ifcool.hystorms.extension.FirebaseExtension.await
import org.d3ifcool.hystorms.extension.FirebaseExtension.awaitRealtime
import org.d3ifcool.hystorms.model.Device
import org.d3ifcool.hystorms.model.Schedule
import org.d3ifcool.hystorms.model.Tank
import org.d3ifcool.hystorms.model.Weather
import org.d3ifcool.hystorms.network.WeatherNetworkMapper
import org.d3ifcool.hystorms.network.WeatherRetrofit
import org.d3ifcool.hystorms.state.DataState

class HomeRepositoryImpl constructor(
    private val weatherService: WeatherRetrofit,
    private val weatherDao: WeatherDao,
    private val networkMapper: WeatherNetworkMapper,
    private val cacheMapper: WeatherCacheMapper,
    private val tankReference: CollectionReference,
    private val deviceReference: CollectionReference,
    private val scheduleReference: CollectionReference
) : HomeRepository {

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
        }.catch {
            emit(DataState.errorThrowable(it))
        }.flowOn(Dispatchers.IO)

    override suspend fun getDevice(deviceId: String): Flow<DataState<Device>> = flow {
        emit(DataState.loading())
        when (val state =
            deviceReference.document(deviceId).get().await()) {
            is DataState.Success -> {
                val snapshot = state.data
                val device = snapshot.toObject(Device::class.java)!!
                device.id = snapshot.id
                emit(DataState.success(device))
            }
            is DataState.Error -> {
                emit(DataState.error(state.exception))
            }
            is DataState.Canceled -> {
                emit(DataState.canceled(state.exception))
            }
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getTanks(userId: String): Flow<DataState<List<Tank>>> = flow {
        emit(DataState.loading())

        val docRef = tankReference.whereEqualTo("owner", userId).whereEqualTo("isAuthorized", true)
        val response = docRef.awaitRealtime()
        if (response.error == null) {
            val tanks = response.packet?.documents?.map { doc ->
                val tank = doc.toObject(Tank::class.java)!!
                tank.id = doc.id
                Action.showLog(tank.id)
                tank
            }
            emit(DataState.success(tanks ?: listOf()))
        } else {
            emit(DataState.error(response.error))
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getSchedules(userId: String, day: Int): Flow<DataState<List<Schedule>>> =
        flow {
            emit(DataState.loading())
            val todayDocRef =
                scheduleReference.whereEqualTo("owner", userId).whereArrayContains("day", day).orderBy("title")
            val responseToday = todayDocRef.awaitRealtime()
            if (responseToday.error == null) {
                val schedules = responseToday.packet?.documents?.map { doc ->
                    val schedule = doc.toObject(Schedule::class.java)!!
                    schedule.id = doc.id.toLong()
                    schedule
                }
                emit(DataState.success(schedules ?: listOf()))
            } else {
                emit(DataState.error(responseToday.error))
            }
        }.catch {
            emit(DataState.errorThrowable(it))
        }.flowOn(Dispatchers.IO)
}
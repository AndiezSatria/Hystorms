package org.d3ifcool.hystorms.repository.device

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
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
import org.d3ifcool.hystorms.model.*
import org.d3ifcool.hystorms.network.WeatherNetworkMapper
import org.d3ifcool.hystorms.network.WeatherRetrofit
import org.d3ifcool.hystorms.state.DataState

class DetailDeviceRepositoryImpl(
    private val tanksReference: CollectionReference,
    private val physicReference: CollectionReference,
    private val userReference: CollectionReference,
    private val deviceRef: CollectionReference,
    private val weatherService: WeatherRetrofit,
    private val weatherDao: WeatherDao,
    private val networkMapper: WeatherNetworkMapper,
    private val cacheMapper: WeatherCacheMapper,
) : DetailDeviceRepository {
    override suspend fun getCondition(conditionId: String): Flow<DataState<Condition>> = flow {
        emit(DataState.loading())

        when (val state = physicReference.document(conditionId).get().await()) {
            is DataState.Canceled -> emit(DataState.error(state.exception))
            is DataState.Error -> emit(DataState.error(state.exception))
            is DataState.Success -> {
                val document = state.data
                emit(
                    DataState.success(
                        document.toObject(Condition::class.java)!!
                    )
                )
            }
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getTanks(deviceId: String): Flow<DataState<List<Tank>>> = flow {
        emit(DataState.loading())

        val docRef = tanksReference.whereEqualTo("device", deviceId)
        val response = docRef.awaitRealtime()
        if (response.error == null) {
            val tanks = response.packet?.documentChanges?.map { doc ->
                val tank = doc.document.toObject(Tank::class.java)
                tank.id = doc.document.id
                tank
            }
            Action.showLog(tanks.toString())
            emit(DataState.success(tanks ?: listOf()))
        } else {
            Action.showLog("error")
            emit(DataState.error(response.error))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun setFavorite(deviceId: String?, userId: String): Flow<DataState<String>> =
        flow {
            emit(DataState.loading())

            when (val state =
                userReference.document(userId).update("favoriteDevice", deviceId).await()) {
                is DataState.Canceled -> emit(DataState.canceled(state.exception))
                is DataState.Error -> emit(DataState.error(state.exception))
                is DataState.Success -> {
                    emit(DataState.success(if (deviceId != null) "Berhasil menambahkan favorit" else "Berhasil menghapus favorit"))
                }
            }
        }.catch {
            emit(DataState.errorThrowable(it))
        }.flowOn(Dispatchers.IO)

    override suspend fun updateLocation(
        address: MyAddress,
        device: Device
    ): Flow<DataState<Device>> = flow {
        emit(DataState.loading())
        val docRef: DocumentReference = deviceRef.document(device.id)
        val update = hashMapOf<String, Any?>(
            "latitude" to address.lat,
            "longitude" to address.lon,
            "address" to address.address,
        )
        when (val state = docRef.update(update).await()) {
            is DataState.Error -> {
                emit(DataState.error(state.exception))
            }
            is DataState.Canceled -> {
                emit(DataState.canceled(state.exception))
            }
            is DataState.Success -> {
                emit(
                    DataState.success(
                        device.copy(
                            latitude = address.lat,
                            longitude = address.lon,
                            address = address.address
                        )
                    )
                )
            }
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)

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
}
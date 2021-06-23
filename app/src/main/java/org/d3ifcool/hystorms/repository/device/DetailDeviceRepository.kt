package org.d3ifcool.hystorms.repository.device

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.d3ifcool.hystorms.BuildConfig
import org.d3ifcool.hystorms.model.*
import org.d3ifcool.hystorms.state.DataState

interface DetailDeviceRepository {
    suspend fun getCondition(conditionId: String): Flow<DataState<Condition>>
    suspend fun getTanks(deviceId: String): Flow<DataState<List<Tank>>>
    suspend fun setFavorite(deviceId: String?, userId: String): Flow<DataState<String>>
    suspend fun getWeather(
        lat: Double,
        lon: Double,
        lang: String
    ): Flow<DataState<Weather>>

    suspend fun updateLocation(
        address: MyAddress,
        device: Device
    ): Flow<DataState<Device>>
}
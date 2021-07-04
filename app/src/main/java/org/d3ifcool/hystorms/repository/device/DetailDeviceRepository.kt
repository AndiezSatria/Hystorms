package org.d3ifcool.hystorms.repository.device

import kotlinx.coroutines.flow.Flow
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
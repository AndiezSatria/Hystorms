package org.d3ifcool.hystorms.repository.home

import kotlinx.coroutines.flow.Flow
import org.d3ifcool.hystorms.model.Device
import org.d3ifcool.hystorms.model.Tank
import org.d3ifcool.hystorms.model.Weather
import org.d3ifcool.hystorms.state.DataState

interface HomeRepository {
    suspend fun getWeather(lat: Double, lon: Double, lang: String): Flow<DataState<Weather>>
    suspend fun getDevice(deviceId: String): Flow<DataState<Device>>
    suspend fun getTanks(userId: String): Flow<DataState<List<Tank>>>
}
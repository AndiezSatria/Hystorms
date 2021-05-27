package org.d3ifcool.hystorms.repository.device

import kotlinx.coroutines.flow.Flow
import org.d3ifcool.hystorms.model.Condition
import org.d3ifcool.hystorms.model.Tank
import org.d3ifcool.hystorms.state.DataState

interface DetailDeviceRepository {
    suspend fun getCondition(conditionId: String): Flow<DataState<Condition>>
    suspend fun getTanks(deviceId: String): Flow<DataState<List<Tank>>>
    suspend fun setFavorite(deviceId: String?, userId: String): Flow<DataState<String>>
}
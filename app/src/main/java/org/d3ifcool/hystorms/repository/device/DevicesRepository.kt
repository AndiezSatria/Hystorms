package org.d3ifcool.hystorms.repository.device

import kotlinx.coroutines.flow.Flow
import org.d3ifcool.hystorms.model.Device
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.state.DataState

interface DevicesRepository {
    suspend fun getDevices(user: User): Flow<DataState<List<Device>>>
}
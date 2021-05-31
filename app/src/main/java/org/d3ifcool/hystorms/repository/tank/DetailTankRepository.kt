package org.d3ifcool.hystorms.repository.tank

import kotlinx.coroutines.flow.Flow
import org.d3ifcool.hystorms.model.Plant
import org.d3ifcool.hystorms.model.Schedule
import org.d3ifcool.hystorms.state.DataState

interface DetailTankRepository {
    suspend fun getPlant(plantId: String): Flow<DataState<Plant>>
    suspend fun getSchedules(tankId: String, day: Int): Flow<DataState<List<Schedule>>>
}
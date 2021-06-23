package org.d3ifcool.hystorms.repository.tank

import kotlinx.coroutines.flow.Flow
import org.d3ifcool.hystorms.model.Schedule
import org.d3ifcool.hystorms.state.DataState

interface AddScheduleRepository {
    suspend fun addSchedule(schedule: Schedule): Flow<DataState<String>>
    suspend fun updateSchedule(schedule: Schedule): Flow<DataState<String>>
}
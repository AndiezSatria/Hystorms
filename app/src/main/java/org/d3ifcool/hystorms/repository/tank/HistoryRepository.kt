package org.d3ifcool.hystorms.repository.tank

import kotlinx.coroutines.flow.Flow
import org.d3ifcool.hystorms.model.History
import org.d3ifcool.hystorms.state.DataState
import java.util.*

interface HistoryRepository {
    suspend fun getHistory(tankId: String, dayStart: Date, dateEnd:Date): Flow<DataState<List<History>>>
    suspend fun getHistoryGraph(tankId: String, dateNow: Date): Flow<DataState<List<History>>>
}
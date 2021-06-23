package org.d3ifcool.hystorms.repository.tank

import kotlinx.coroutines.flow.Flow
import org.d3ifcool.hystorms.model.Tank
import org.d3ifcool.hystorms.state.DataState
import java.io.File

interface EditTankRepository {
    suspend fun updateTank(tank: Tank): Flow<DataState<String>>
    suspend fun uploadImageAndGetUrl(tank: Tank, file: File): Flow<DataState<Tank>>
}
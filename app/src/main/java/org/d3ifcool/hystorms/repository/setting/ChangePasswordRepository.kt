package org.d3ifcool.hystorms.repository.setting

import kotlinx.coroutines.flow.Flow
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.state.DataState

interface ChangePasswordRepository {
    suspend fun changePassword(pass: String): Flow<DataState<String>>
}
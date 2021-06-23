package org.d3ifcool.hystorms.repository.setting

import kotlinx.coroutines.flow.Flow
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.state.DataState
import java.io.File

interface EditProfileRepository {
    suspend fun uploadImageAndGetUrl(user: User, file: File): Flow<DataState<User>>
    suspend fun updateUser(user: User): Flow<DataState<String>>
}
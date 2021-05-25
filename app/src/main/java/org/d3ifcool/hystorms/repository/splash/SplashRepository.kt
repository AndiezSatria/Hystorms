package org.d3ifcool.hystorms.repository.splash

import kotlinx.coroutines.flow.Flow
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.state.DataState

interface SplashRepository {
    suspend fun getUserDataFromFirestore(uid: String): Flow<DataState<User>>
}
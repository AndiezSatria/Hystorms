package org.d3ifcool.hystorms.repository.auth

import kotlinx.coroutines.flow.Flow
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.state.DataState
import java.io.File

interface AuthenticationRepository {
    suspend fun signUpUser(user: User, password: String): Flow<DataState<User>>
    suspend fun uploadImageToStorage(file: File, user: User): Flow<DataState<User>>
    suspend fun createUserInFirestore(user: User): Flow<DataState<User>>
    suspend fun signInUser(email: String, password: String): Flow<DataState<String>>
    suspend fun getUser(uid: String): Flow<DataState<User>>
    suspend fun getUserLogin(uid: String, token: String): Flow<DataState<User>>
    suspend fun resetPassword(email: String): Flow<DataState<String>>
}
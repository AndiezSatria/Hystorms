package org.d3ifcool.hystorms.repository

import com.google.firebase.auth.FirebaseAuthEmailException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.repository.auth.AuthenticationRepository
import org.d3ifcool.hystorms.state.DataState
import kotlinx.coroutines.delay
import java.io.File

class FakeAuthRepositoryImpl: AuthenticationRepository {
    private val userFake = User()

    override suspend fun signUpUser(user: User, password: String): Flow<DataState<User>> = flow{
        emit(DataState.loading())
        delay(3000)
        emit(DataState.success(user))
    }

    override suspend fun uploadImageToStorage(file: File, user: User): Flow<DataState<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun createUserInFirestore(user: User): Flow<DataState<User>> = flow {
        emit(DataState.loading())
        delay(3000)
        emit(DataState.success(user))
    }

    override suspend fun signInUser(email: String, password: String): Flow<DataState<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(uid: String): Flow<DataState<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun resetPassword(email: String): Flow<DataState<String>> {
        TODO("Not yet implemented")
    }
}
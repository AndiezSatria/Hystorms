package org.d3ifcool.hystorms.repository.setting

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.d3ifcool.hystorms.extension.FirebaseExtension.await
import org.d3ifcool.hystorms.state.DataState

class ChangePasswordRepositoryImpl constructor(
    private val firebaseAuth: FirebaseAuth
) : ChangePasswordRepository {
    override suspend fun changePassword(pass: String): Flow<DataState<String>> = flow {
        emit(DataState.loading())
        val user = firebaseAuth.currentUser
        when (val state = user.updatePassword(pass).await()) {
            is DataState.Canceled -> emit(DataState.canceled(state.exception))
            is DataState.Error -> emit(DataState.error(state.exception))
            is DataState.Success -> emit(DataState.success("Berhasil mengubah password."))
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)
}
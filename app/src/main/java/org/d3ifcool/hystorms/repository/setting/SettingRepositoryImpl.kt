package org.d3ifcool.hystorms.repository.setting

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.d3ifcool.hystorms.extension.FirebaseExtension.await
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.state.DataState

class SettingRepositoryImpl constructor(
    private val userRef: CollectionReference,
    private val firebaseAuth: FirebaseAuth
) : SettingRepository {
    override suspend fun getUser(uid: String): Flow<DataState<User>> = flow {
        emit(DataState.loading())
        when (val state = userRef.document(uid).get().await()) {
            is DataState.Canceled -> emit(DataState.canceled(state.exception))
            is DataState.Error -> emit(DataState.canceled(state.exception))
            is DataState.Success -> {
                val user = state.data.toObject(User::class.java)!!
                emit(DataState.success(user))
            }
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun logout() {
        firebaseAuth.signOut()
    }
}
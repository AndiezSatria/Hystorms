package org.d3ifcool.hystorms.repository.device

import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.extension.FirebaseExtension.await
import org.d3ifcool.hystorms.extension.FirebaseExtension.awaitRealtime
import org.d3ifcool.hystorms.model.Condition
import org.d3ifcool.hystorms.model.Tank
import org.d3ifcool.hystorms.state.DataState

class DetailDeviceRepositoryImpl(
    private val tanksReference: CollectionReference,
    private val physicReference: CollectionReference,
    private val userReference: CollectionReference
) : DetailDeviceRepository {
    override suspend fun getCondition(conditionId: String): Flow<DataState<Condition>> = flow {
        emit(DataState.loading())

        when (val state = physicReference.document(conditionId).get().await()) {
            is DataState.Canceled -> emit(DataState.error(state.exception))
            is DataState.Error -> emit(DataState.error(state.exception))
            is DataState.Success -> {
                val document = state.data
                emit(
                    DataState.success(
                        document.toObject(Condition::class.java)!!
                    )
                )
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getTanks(deviceId: String): Flow<DataState<List<Tank>>> = flow {
        emit(DataState.loading())

        val docRef = tanksReference.whereEqualTo("device", deviceId)
        val response = docRef.awaitRealtime()
        if (response.error == null) {
            val tanks = response.packet?.documentChanges?.map { doc ->
                val tank = doc.document.toObject(Tank::class.java)
                tank.id = doc.document.id
                tank
            }
            Action.showLog(tanks.toString())
            emit(DataState.success(tanks ?: listOf()))
        } else {
            Action.showLog("error")
            emit(DataState.error(response.error))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun setFavorite(deviceId: String?, userId: String): Flow<DataState<String>> =
        flow {
            emit(DataState.loading())

            when (val state =
                userReference.document(userId).update("favoriteDevice", deviceId).await()) {
                is DataState.Canceled -> emit(DataState.canceled(state.exception))
                is DataState.Error -> emit(DataState.error(state.exception))
                is DataState.Success -> {
                    emit(DataState.success(if (deviceId != null) "Berhasil menambahkan favorit" else "Berhasil menghapus favorit"))
                }
            }
        }
}
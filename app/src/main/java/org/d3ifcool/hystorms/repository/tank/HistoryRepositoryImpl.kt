package org.d3ifcool.hystorms.repository.tank

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.extension.FirebaseExtension.await
import org.d3ifcool.hystorms.model.History
import org.d3ifcool.hystorms.state.DataState
import java.util.*

class HistoryRepositoryImpl constructor(private val historyRef: CollectionReference) :
    HistoryRepository {
    override suspend fun getHistory(
        tankId: String,
        dayStart: Date,
        dateEnd: Date
    ): Flow<DataState<List<History>>> = flow {
        emit(DataState.loading())
        Action.showLog(dayStart.toString())
        val docRef =
            historyRef.whereEqualTo("tank", tankId).whereGreaterThanOrEqualTo("timestamp", dayStart)
                .whereLessThanOrEqualTo("timestamp", dateEnd)
                .orderBy("timestamp", Query.Direction.ASCENDING)
        when (val state = docRef.get().await()) {
            is DataState.Canceled -> emit(DataState.canceled(state.exception))
            is DataState.Error -> emit(DataState.error(state.exception))
            is DataState.Success -> {
                val histories = state.data.toObjects(History::class.java)
                emit(DataState.success(histories))
            }
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getHistoryGraph(
        tankId: String,
        dateNow: Date
    ): Flow<DataState<List<History>>> = flow {
        emit(DataState.loading())
        val docRef =
            historyRef.whereEqualTo("tank", tankId).whereLessThanOrEqualTo("timestamp", dateNow)
                .orderBy("timestamp", Query.Direction.ASCENDING).limit(150)
        when (val state = docRef.get().await()) {
            is DataState.Canceled -> emit(DataState.canceled(state.exception))
            is DataState.Error -> emit(DataState.error(state.exception))
            is DataState.Success -> {
                val histories = state.data.toObjects(History::class.java)
                emit(DataState.success(histories))
            }
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)
}
package org.d3ifcool.hystorms.repository.tank

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.d3ifcool.hystorms.extension.FirebaseExtension.await
import org.d3ifcool.hystorms.model.Schedule
import org.d3ifcool.hystorms.state.DataState

class AddScheduleRepositoryImpl constructor(
    private val scheduleRef: CollectionReference
) : AddScheduleRepository {
    override suspend fun addSchedule(schedule: Schedule): Flow<DataState<String>> = flow {
        emit(DataState.loading())
        when (val state = scheduleRef.document(schedule.id.toString()).set(schedule).await()) {
            is DataState.Error -> {
                emit(DataState.error(state.exception))
            }
            is DataState.Canceled -> {
                emit(DataState.canceled(state.exception))
            }
            is DataState.Success -> {
                emit(DataState.success("Berhasil menambah data."))
            }
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun updateSchedule(schedule: Schedule): Flow<DataState<String>> = flow {
        emit(DataState.loading())
        val docRef: DocumentReference = scheduleRef.document(schedule.id.toString())
        val update = hashMapOf<String, Any?>(
            "title" to schedule.title,
            "day" to schedule.day,
            "time" to schedule.time
        )
        when (val state = docRef.update(update).await()) {
            is DataState.Error -> {
                emit(DataState.error(state.exception))
            }
            is DataState.Canceled -> {
                emit(DataState.canceled(state.exception))
            }
            is DataState.Success -> {
                emit(DataState.success("Berhasil mengubah data."))
            }
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)
}
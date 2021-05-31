package org.d3ifcool.hystorms.repository.tank

import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.d3ifcool.hystorms.extension.FirebaseExtension.await
import org.d3ifcool.hystorms.extension.FirebaseExtension.awaitRealtime
import org.d3ifcool.hystorms.model.Plant
import org.d3ifcool.hystorms.model.Schedule
import org.d3ifcool.hystorms.state.DataState

class DetailTankRepositoryImpl constructor(
    private val plantRef: CollectionReference,
    private val scheduleRef: CollectionReference
) : DetailTankRepository {
    override suspend fun getPlant(plantId: String): Flow<DataState<Plant>> = flow {
        emit(DataState.loading())

        when (val state = plantRef.document(plantId).get().await()) {
            is DataState.Canceled -> {
                emit(DataState.canceled(state.exception))
            }
            is DataState.Error -> {
                emit(DataState.canceled(state.exception))
            }
            is DataState.Success -> {
                val data = state.data.toObject(Plant::class.java)!!
                emit(DataState.success(data))
            }
        }
    }

    override suspend fun getSchedules(tankId: String, day: Int): Flow<DataState<List<Schedule>>> =
        flow {
            emit(DataState.loading())
            val todayDocRef =
                scheduleRef.whereEqualTo("tank", tankId).whereEqualTo("day", day)
            val responseToday = todayDocRef.awaitRealtime()
            if (responseToday.error == null) {
                val schedules = responseToday.packet?.documents?.map { doc ->
                    val schedule = doc.toObject(Schedule::class.java)!!
                    schedule.id = doc.id
                    schedule
                }
                emit(DataState.success(schedules ?: listOf()))
            } else {
                emit(DataState.error(responseToday.error))
            }
        }
}
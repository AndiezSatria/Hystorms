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
import org.d3ifcool.hystorms.extension.FirebaseExtension.awaitRealtime
import org.d3ifcool.hystorms.model.Plant
import org.d3ifcool.hystorms.model.Schedule
import org.d3ifcool.hystorms.model.Tank
import org.d3ifcool.hystorms.state.DataState

class DetailTankRepositoryImpl constructor(
    private val plantRef: CollectionReference,
    private val tankRef: CollectionReference,
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

    override suspend fun getTank(tankId: String): Flow<DataState<Tank>> = flow {
        emit(DataState.loading())
        when (val state = tankRef.document(tankId).get().await()) {
            is DataState.Canceled -> emit(DataState.canceled(state.exception))
            is DataState.Error -> emit(DataState.error(state.exception))
            is DataState.Success -> {
                val tank = state.data.toObject(Tank::class.java)!!
                Action.showLog(tank.toString())
                emit(DataState.success(tank))
            }
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getSchedules(tankId: String, day: Int): Flow<DataState<List<Schedule>>> =
        flow {
            emit(DataState.loading())
            val todayDocRef =
                scheduleRef.whereEqualTo("tank", tankId).whereArrayContains("day", day)
                    .orderBy("title", Query.Direction.ASCENDING)
            when(val responseToday = todayDocRef.get().await()) {
                is DataState.Canceled -> {
                    emit(DataState.canceled(responseToday.exception))
                }
                is DataState.Error -> {
                    emit(DataState.error(responseToday.exception))
                }
                is DataState.Success -> {
                    val schedules = responseToday.data.toObjects(Schedule::class.java)
                    emit(DataState.success(schedules))
                }
            }
//            if (responseToday.error == null) {
//                val schedules = responseToday.packet?.documents?.map { doc ->
//                    val schedule = doc.toObject(Schedule::class.java)!!
//                    schedule.id = doc.id.toLong()
//                    schedule
//                }
//                emit(DataState.success(schedules ?: listOf()))
//            } else {
//                emit(DataState.error(responseToday.error))
//            }
        }.catch {
            emit(DataState.errorThrowable(it))
        }.flowOn(Dispatchers.IO)

    override suspend fun deleteSchedule(listOfId: List<Long>): Flow<DataState<String>> = flow {
        var iterator = 0
        listOfId.forEach { id ->
            when (val state = scheduleRef.document(id.toString()).delete().await()) {
                is DataState.Canceled -> emit(DataState.canceled(state.exception))
                is DataState.Error -> emit(DataState.error(state.exception))
                is DataState.Success -> {
                    if (++iterator == listOfId.size) emit(DataState.success("$iterator data berhasil dihapus."))
                }
            }
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)
}
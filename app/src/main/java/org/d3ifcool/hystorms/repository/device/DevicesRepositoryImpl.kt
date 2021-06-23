package org.d3ifcool.hystorms.repository.device

import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.extension.FirebaseExtension.await
import org.d3ifcool.hystorms.extension.FirebaseExtension.awaitRealtime
import org.d3ifcool.hystorms.model.AddDevice
import org.d3ifcool.hystorms.model.Device
import org.d3ifcool.hystorms.model.Tank
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.state.DataState

class DevicesRepositoryImpl constructor(
    private val devicesReference: CollectionReference,
    private val tankRef: CollectionReference
) : DevicesRepository {

    override suspend fun getDevices(user: User): Flow<DataState<List<Device>>> = flow {
        emit(DataState.loading())
        val docRef =
            devicesReference.whereEqualTo("owner", user.uid).whereEqualTo("isAuthorized", true)
        val response = docRef.awaitRealtime()
        if (response.error == null) {
            val devices = response.packet?.documentChanges?.map { doc ->
                Action.showLog(doc.toString())
                val device = doc.document.toObject(Device::class.java)
                device.id = doc.document.id
                device
            }
            emit(DataState.success(devices ?: listOf()))
        } else {
            Action.showLog("error")
            emit(DataState.error(response.error))
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getDevice(addDevice: AddDevice): Flow<DataState<Device>> = flow {
        emit(DataState.loading())
        val docRef = devicesReference.whereEqualTo("name", addDevice.name)
            .whereEqualTo("macAddress", addDevice.macAddress).limit(1)
        when (val state = docRef.get().await()) {
            is DataState.Canceled -> emit(DataState.canceled(state.exception))
            is DataState.Error -> emit(DataState.error(state.exception))
            is DataState.Success -> {
                try {
                    val device = state.data.toObjects(Device::class.java)[0]
                    emit(DataState.success(device))
                } catch (e: IndexOutOfBoundsException) {
                    emit(DataState.error(e))
                }
            }
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun updateOwner(uid: String, deviceId: String): Flow<DataState<String>> =
        flow {
            emit(DataState.loading())
            val update = hashMapOf<String, Any>(
                "owner" to uid,
                "isAuthorized" to false
            )
            val task = devicesReference.document(deviceId).update(update)
            when (val state = task.await()) {
                is DataState.Canceled -> emit(DataState.canceled(state.exception))
                is DataState.Error -> emit(DataState.canceled(state.exception))
                is DataState.Success -> emit(DataState.Success("Alat berhasil didaftarkan."))
            }
        }.catch {
            emit(DataState.errorThrowable(it))
        }.flowOn(Dispatchers.IO)

    override suspend fun deleteOwnership(listOfDeviceId: List<String>): Flow<DataState<String>> =
        flow {
            emit(DataState.loading())
            var iterator = 0
            listOfDeviceId.forEach { id ->
                val deviceUpdate = hashMapOf<String, Any?>(
                    "owner" to null,
                    "isAuthorized" to false
                )
                when (val state = devicesReference.document(id).update(deviceUpdate).await()) {
                    is DataState.Canceled -> emit(DataState.canceled(state.exception))
                    is DataState.Error -> emit(DataState.error(state.exception))
                    is DataState.Success -> {
                        when (val dataState = tankRef.whereEqualTo("device", id).get().await()) {
                            is DataState.Canceled -> emit(DataState.canceled(dataState.exception))
                            is DataState.Error -> emit(DataState.error(dataState.exception))
                            is DataState.Success -> {
                                val tanks = dataState.data.toObjects(Tank::class.java)
                                if (tanks.isNotEmpty()) {
                                    var iterator2 = 0
                                    tanks.forEach { tank ->
                                        val tankUpdate = hashMapOf<String, Any?>(
                                            "owner" to null,
                                            "isAuthorized" to false
                                        )
                                        when (val tankState =
                                            tankRef.document(tank.id).update(tankUpdate).await()) {
                                            is DataState.Canceled -> emit(
                                                DataState.canceled(
                                                    tankState.exception
                                                )
                                            )
                                            is DataState.Error -> emit(DataState.error(tankState.exception))
                                            is DataState.Success -> {
                                                if (++iterator == listOfDeviceId.size && ++iterator2 == tanks.size) emit(
                                                    DataState.success("$iterator alat berhasil dihapus.")
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.catch {
            emit(DataState.errorThrowable(it))
        }.flowOn(Dispatchers.IO)
}
package org.d3ifcool.hystorms.repository.device

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.extension.FirebaseExtension.asFlow
import org.d3ifcool.hystorms.extension.FirebaseExtension.awaitRealtime
import org.d3ifcool.hystorms.model.DataOrException
import org.d3ifcool.hystorms.model.Device
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.ViewState

class DevicesRepositoryImpl constructor(
    private val devicesReference: CollectionReference
) : DevicesRepository {

    override suspend fun getDevices(user: User): Flow<DataState<List<Device>>> = flow {
        emit(DataState.loading())

        val docRef = devicesReference.whereEqualTo("owner", user.uid)
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
    }
}
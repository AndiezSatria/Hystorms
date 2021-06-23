package org.d3ifcool.hystorms.repository.tank

import android.net.Uri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.extension.FirebaseExtension.await
import org.d3ifcool.hystorms.model.Tank
import org.d3ifcool.hystorms.state.DataState
import java.io.File

class EditTankRepositoryImpl(
    private val tankRef: CollectionReference,
    private val storageRef: FirebaseStorage
) : EditTankRepository {
    override suspend fun updateTank(tank: Tank): Flow<DataState<String>> = flow {
        emit(DataState.loading())
        val docRef: DocumentReference = tankRef.document(tank.id)
        val update = hashMapOf<String, Any?>(
            "name" to tank.name,
            "amount" to tank.amount,
            "plantedAt" to tank.plantedAt,
            "photoUrl" to tank.photoUrl
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

    override suspend fun uploadImageAndGetUrl(tank: Tank, file: File): Flow<DataState<Tank>> =
        flow {
            emit(DataState.loading())
            val fileRef = storageRef.reference.child(
                "${Constant.TANK}/${file.name}"
            )
            when (val uploadTask = fileRef.putFile(Uri.fromFile(file)).await()) {
                is DataState.Success -> {
                    when (val downloadUrlTask = uploadTask.data.storage.downloadUrl.await()) {
                        is DataState.Success -> {
                            val downloadUrl = downloadUrlTask.data.toString()
                            emit(DataState.success(tank.copy(photoUrl = downloadUrl)))
                        }
                        is DataState.Error -> {
                            emit(DataState.error(downloadUrlTask.exception))
                        }
                        is DataState.Canceled -> {
                            emit(DataState.canceled(downloadUrlTask.exception))
                        }
                    }
                }
                is DataState.Error -> {
                    emit(DataState.error(uploadTask.exception))
                }
                is DataState.Canceled -> {
                    emit(DataState.canceled(uploadTask.exception))
                }
            }
        }.catch {
            emit(DataState.errorThrowable(it))
        }.flowOn(Dispatchers.IO)
}
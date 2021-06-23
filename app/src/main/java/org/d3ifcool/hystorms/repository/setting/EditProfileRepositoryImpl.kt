package org.d3ifcool.hystorms.repository.setting

import android.net.Uri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.extension.FirebaseExtension.await
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.state.DataState
import java.io.File

class EditProfileRepositoryImpl constructor(
    private val userRef: CollectionReference,
    private val storageRef: FirebaseStorage
) : EditProfileRepository {
    override suspend fun uploadImageAndGetUrl(user: User, file: File): Flow<DataState<User>> =
        flow {
            emit(DataState.loading())
            val fileRef = storageRef.reference.child(
                "${Constant.PROFILE_PICTURE}/${file.name}"
            )
            when (val uploadTask = fileRef.putFile(Uri.fromFile(file)).await()) {
                is DataState.Success -> {
                    when (val downloadUrlTask = uploadTask.data.storage.downloadUrl.await()) {
                        is DataState.Success -> {
                            val downloadUrl = downloadUrlTask.data.toString()
                            emit(DataState.success(user.copy(photoUrl = downloadUrl)))
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

        }.flowOn(Dispatchers.IO)

    override suspend fun updateUser(user: User): Flow<DataState<String>> = flow {
        emit(DataState.loading())
        val uidRef: DocumentReference = userRef.document(user.uid)
        val update = hashMapOf(
            "modifiedAt" to FieldValue.serverTimestamp(),
            "name" to user.name,
            "photoUrl" to user.photoUrl
        )
        when (val setTask = uidRef.update(update).await()) {
            is DataState.Success -> {
                emit(DataState.success("Berhasil Mengubah Data"))
            }
            is DataState.Error -> {
                emit(DataState.error(setTask.exception))
            }
            is DataState.Canceled -> {
                emit(DataState.canceled(setTask.exception))
            }
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)

}
package org.d3ifcool.hystorms.repository.auth

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.extension.FirebaseExtension.await
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.state.DataState
import java.io.File

class AuthenticationRepositoryImpl constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRef: CollectionReference,
    private val storageRef: FirebaseStorage
) : AuthenticationRepository {
    override suspend fun signUpUser(user: User, password: String): Flow<DataState<User>> {
        return flow {
            emit(DataState.loading())
            when (val state =
                firebaseAuth.createUserWithEmailAndPassword(user.email, password).await()) {
                is DataState.Success -> {
                    val firebaseUser = state.data.user
                    if (firebaseUser != null) user.uid = firebaseUser.uid
                    emit(DataState.success(user))
                }
                is DataState.Error -> {
                    emit(DataState.error(state.exception))
                }
                is DataState.Canceled -> {
                    emit(DataState.canceled(state.exception))
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun uploadImageToStorage(file: File, user: User): Flow<DataState<User>> = flow {
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

    override suspend fun createUserInFirestore(user: User): Flow<DataState<User>> = flow {
        emit(DataState.loading())
        val uidRef: DocumentReference = userRef.document(user.uid)
        when (val setTask = uidRef.set(user).await()) {
            is DataState.Success -> {
                emit(DataState.success(user))
            }
            is DataState.Error -> {
                emit(DataState.error(setTask.exception))
            }
            is DataState.Canceled -> {
                emit(DataState.canceled(setTask.exception))
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun signInUser(email: String, password: String): Flow<DataState<String>> = flow {
        emit(DataState.loading())

        when (val state = firebaseAuth.signInWithEmailAndPassword(email, password).await()) {
            is DataState.Success -> {
                val firebaseUser = state.data.user
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    emit(DataState.success(uid))
                }
            }
            is DataState.Error -> {
                emit(DataState.error(state.exception))
            }
            is DataState.Canceled -> {
                emit(DataState.canceled(state.exception))
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getUser(uid: String): Flow<DataState<User>> = flow {
        emit(DataState.loading())

        when (val state = userRef.document(uid).get().await()) {
            is DataState.Success -> {
                val user = state.data.toObject(User::class.java)!!
                emit(DataState.success(user))
            }
            is DataState.Error -> {
                emit(DataState.error(state.exception))
            }
            is DataState.Canceled -> {
                emit(DataState.canceled(state.exception))
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun resetPassword(email: String): Flow<DataState<String>> = flow {
        emit(DataState.loading())

        when (val state = firebaseAuth.sendPasswordResetEmail(email).await()) {
            is DataState.Success -> {
                val message = "Email berhasil dikirim."
                emit(DataState.success(message))
            }
            is DataState.Error -> {
                emit(DataState.error(state.exception))
            }
            is DataState.Canceled -> {
                emit(DataState.canceled(state.exception))
            }
        }
    }.flowOn(Dispatchers.IO)
}
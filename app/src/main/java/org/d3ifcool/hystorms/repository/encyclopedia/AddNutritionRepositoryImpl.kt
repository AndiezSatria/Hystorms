package org.d3ifcool.hystorms.repository.encyclopedia

import android.net.Uri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.extension.FirebaseExtension.await
import org.d3ifcool.hystorms.model.Nutrition
import org.d3ifcool.hystorms.state.DataState
import java.io.File

class AddNutritionRepositoryImpl constructor(
    private val storage: FirebaseStorage,
    private val nutritionRef: CollectionReference
) : AddNutritionRepository {
    override suspend fun uploadImage(file: File, nutrition: Nutrition): Flow<DataState<Nutrition>> =
        flow {
            emit(DataState.loading())
            val fileRef = storage.reference.child("${Constant.NUTRITION_IMAGE}/${file.name}")
            when (val state = fileRef.putFile(Uri.fromFile(file)).await()) {
                is DataState.Success -> {
                    when (val downloadUrlTask = state.data.storage.downloadUrl.await()) {
                        is DataState.Success -> {
                            val downloadUrl = downloadUrlTask.data.toString()
                            emit(DataState.success(nutrition.copy(photoUrl = downloadUrl)))
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
                    emit(DataState.error(state.exception))
                }
                is DataState.Canceled -> {
                    emit(DataState.canceled(state.exception))
                }
            }
        }.catch {
            emit(DataState.errorThrowable(it))
        }.flowOn(Dispatchers.IO)

    override suspend fun setNutrition(nutrition: Nutrition): Flow<DataState<String>> = flow {
        emit(DataState.loading())
        when (val state = nutritionRef.add(nutrition).await()) {
            is DataState.Error -> {
                emit(DataState.error(state.exception))
            }
            is DataState.Canceled -> {
                emit(DataState.canceled(state.exception))
            }
            is DataState.Success -> {
                val id = state.data.id
                when (val updateId = nutritionRef.document(id).update("id", id).await()) {
                    is DataState.Error -> {
                        emit(DataState.error(updateId.exception))
                    }
                    is DataState.Canceled -> {
                        emit(DataState.canceled(updateId.exception))
                    }
                    is DataState.Success -> {
                        emit(DataState.success("Berhasil menambahkan data."))
                    }
                }
            }
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun updateNutrition(nutrition: Nutrition): Flow<DataState<String>> = flow {
        emit(DataState.loading())
        Action.showLog(nutrition.id)
        val docRef: DocumentReference = nutritionRef.document(nutrition.id)
        val update = hashMapOf<String, Any?>(
            "name" to nutrition.name,
            "nutrientContent" to nutrition.nutrientContent,
            "usage" to nutrition.usage,
            "photoUrl" to nutrition.photoUrl,
            "ppm" to nutrition.ppm,
            "effect" to nutrition.effect,
            "description" to nutrition.description
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
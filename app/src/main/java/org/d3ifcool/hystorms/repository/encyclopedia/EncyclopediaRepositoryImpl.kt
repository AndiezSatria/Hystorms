package org.d3ifcool.hystorms.repository.encyclopedia

import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.d3ifcool.hystorms.extension.FirebaseExtension.await
import org.d3ifcool.hystorms.extension.FirebaseExtension.awaitRealtime
import org.d3ifcool.hystorms.model.Nutrition
import org.d3ifcool.hystorms.model.Plant
import org.d3ifcool.hystorms.state.DataState

class EncyclopediaRepositoryImpl constructor(
    private val plantReference: CollectionReference,
    private val nutritionReference: CollectionReference
) : EncyclopediaRepository {
    override suspend fun getPlants(): Flow<DataState<List<Plant>>> = flow {
        emit(DataState.loading())
        val response = plantReference.orderBy("name").awaitRealtime()
        if (response.error == null) {
            val plants = response.packet?.documents?.map { doc ->
                val plant = doc.toObject(Plant::class.java)!!
                plant.id = doc.id
                plant
            }
            emit(DataState.success(plants ?: listOf()))
        } else {
            emit(DataState.error(response.error))
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getNutrition(uid: String): Flow<DataState<List<Nutrition>>> = flow {
        emit(DataState.loading())
        val docRef = nutritionReference.whereEqualTo("owner", uid).orderBy("name")
        val response = docRef.awaitRealtime()
        if (response.error == null) {
            val nutrition = response.packet?.documents?.map { doc ->
                val nutrition = doc.toObject(Nutrition::class.java)!!
                nutrition.id = doc.id
                nutrition
            }
            emit(DataState.success(nutrition ?: listOf()))
        } else {
            emit(DataState.error(response.error))
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getNutrition(): Flow<DataState<List<Nutrition>>> = flow {
        emit(DataState.loading())
        val docRef = nutritionReference.whereEqualTo("owner", null).orderBy("name")
        val response = docRef.awaitRealtime()
        if (response.error == null) {
            val nutrition = response.packet?.documents?.map { doc ->
                val nutrition = doc.toObject(Nutrition::class.java)!!
                nutrition.id = doc.id
                nutrition
            }
            emit(DataState.success(nutrition ?: listOf()))
        } else {
            emit(DataState.error(response.error))
        }
    }.catch {
        emit(DataState.errorThrowable(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteItems(listOfId: List<String>): Flow<DataState<String>> = flow {
        emit(DataState.loading())
        var iterator = 0
        listOfId.forEach { id ->
            when (val state = nutritionReference.document(id).delete().await()) {
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
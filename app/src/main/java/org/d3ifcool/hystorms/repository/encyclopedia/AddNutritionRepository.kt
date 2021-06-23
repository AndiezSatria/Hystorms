package org.d3ifcool.hystorms.repository.encyclopedia

import kotlinx.coroutines.flow.Flow
import org.d3ifcool.hystorms.model.Nutrition
import org.d3ifcool.hystorms.state.DataState
import java.io.File

interface AddNutritionRepository {
    suspend fun uploadImage(file: File, nutrition: Nutrition): Flow<DataState<Nutrition>>
    suspend fun setNutrition(nutrition: Nutrition): Flow<DataState<String>>
    suspend fun updateNutrition(nutrition: Nutrition): Flow<DataState<String>>
}
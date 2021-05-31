package org.d3ifcool.hystorms.repository.encyclopedia

import kotlinx.coroutines.flow.Flow
import org.d3ifcool.hystorms.model.Nutrition
import org.d3ifcool.hystorms.model.Plant
import org.d3ifcool.hystorms.state.DataState

interface EncyclopediaRepository {
    suspend fun getPlants(): Flow<DataState<List<Plant>>>
    suspend fun getNutrition(): Flow<DataState<List<Nutrition>>>
}
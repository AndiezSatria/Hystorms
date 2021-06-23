package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.d3ifcool.hystorms.model.Plant
import javax.inject.Inject

@HiltViewModel
class DetailPlantViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _plant: MutableLiveData<Plant> = savedStateHandle.getLiveData("plant")
    val plant: LiveData<Plant> = _plant
}
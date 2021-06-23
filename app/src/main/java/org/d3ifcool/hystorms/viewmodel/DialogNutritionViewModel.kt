package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.d3ifcool.hystorms.model.Nutrition
import javax.inject.Inject

@HiltViewModel
class DialogNutritionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _nutrition: MutableLiveData<Nutrition> = savedStateHandle.getLiveData("nutrition")
    val nutrition: LiveData<Nutrition> = _nutrition
}
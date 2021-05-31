package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.model.Nutrition
import org.d3ifcool.hystorms.repository.encyclopedia.EncyclopediaRepositoryImpl
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val repository: EncyclopediaRepositoryImpl
) : ViewModel() {

    fun doneShowingMessage() {
        _snackBarMessage.value = null
    }

    private val _viewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.LOADING)
    val viewState: LiveData<ViewState> = _viewState

    private val _snackBarMessage: MutableLiveData<String> = MutableLiveData()
    val snackBarMessage: LiveData<String> = _snackBarMessage

    private val _nutrition: MutableLiveData<List<Nutrition>> = MutableLiveData()
    val nutrition: LiveData<List<Nutrition>> = _nutrition

    init {
        viewModelScope.launch {
            repository.getNutrition().onEach { dataState ->
                when (dataState) {
                    is DataState.Canceled -> {
                        _snackBarMessage.value = dataState.exception.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.Error -> {
                        _snackBarMessage.value = dataState.exception.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.Loading -> _viewState.value = ViewState.LOADING
                    is DataState.Success -> {
                        _viewState.value = ViewState.SUCCESS
                        _nutrition.value = dataState.data
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}
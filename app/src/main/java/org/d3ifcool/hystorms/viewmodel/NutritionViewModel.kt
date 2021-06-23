package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.model.Nutrition
import org.d3ifcool.hystorms.repository.encyclopedia.EncyclopediaRepository
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val repository: EncyclopediaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    fun doneShowingMessage() {
        _snackBarMessage.value = null
    }

    private val _uid: MutableLiveData<String> = savedStateHandle.getLiveData(Constant.USER)
    val uid: LiveData<String> = _uid

    private val _viewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.LOADING)
    val viewState: LiveData<ViewState> = _viewState

    private val _snackBarMessage: MutableLiveData<String> = MutableLiveData()
    val snackBarMessage: LiveData<String> = _snackBarMessage

    private val _nutrition: MutableLiveData<List<Nutrition>> = MutableLiveData()
    val nutrition: LiveData<List<Nutrition>> = _nutrition

    fun getNutrition(uid: String) {
        viewModelScope.launch {
            repository.getNutrition(uid).onEach { dataState ->
                when (dataState) {
                    is DataState.Canceled -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackBarMessage.value = dataState.exception.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.Error -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackBarMessage.value = dataState.exception.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.Loading -> {
                        EspressoIdlingResource.increment()
                        _viewState.value = ViewState.LOADING
                    }
                    is DataState.Success -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        repository.getNutrition().onEach { state ->
                            when (state) {
                                is DataState.Canceled -> {
                                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                                        EspressoIdlingResource.decrement() // Set app as idle.
                                    }
                                    _snackBarMessage.value = state.exception.message
                                    _viewState.value = ViewState.ERROR
                                }
                                is DataState.Error -> {
                                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                                        EspressoIdlingResource.decrement() // Set app as idle.
                                    }
                                    _snackBarMessage.value = state.exception.message
                                    _viewState.value = ViewState.ERROR
                                }
                                is DataState.Loading -> {
                                    EspressoIdlingResource.increment()
                                    _viewState.value = ViewState.LOADING
                                }
                                is DataState.Success -> {
                                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                                        EspressoIdlingResource.decrement() // Set app as idle.
                                    }
                                    _viewState.value = ViewState.SUCCESS
                                    val listNutrition = arrayListOf<Nutrition>()
                                    listNutrition.addAll(state.data)
                                    listNutrition.addAll(dataState.data)
                                    _nutrition.value = listNutrition
                                }
                                is DataState.ErrorThrowable -> {
                                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                                        EspressoIdlingResource.decrement() // Set app as idle.
                                    }
                                    _snackBarMessage.value = state.throwable.message
                                    _viewState.value = ViewState.ERROR
                                }
                            }
                        }.launchIn(viewModelScope)
                    }
                    is DataState.ErrorThrowable -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackBarMessage.value = dataState.throwable.message
                        _viewState.value = ViewState.ERROR
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun deleteNutrition(listOfId: List<String>) {
        viewModelScope.launch {
            repository.deleteItems(listOfId).onEach { dataState ->
                when (dataState) {
                    is DataState.Canceled -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackBarMessage.value = dataState.exception.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.Error -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackBarMessage.value = dataState.exception.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.Loading -> {
                        EspressoIdlingResource.increment()
                        _viewState.value = ViewState.LOADING
                    }
                    is DataState.Success -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _viewState.value = ViewState.SUCCESS
                        getNutrition(_uid.value!!)
                        _snackBarMessage.value = dataState.data
                    }
                    is DataState.ErrorThrowable -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackBarMessage.value = dataState.throwable.message
                        _viewState.value = ViewState.ERROR
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}
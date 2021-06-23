package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.model.Plant
import org.d3ifcool.hystorms.repository.encyclopedia.EncyclopediaRepository
import org.d3ifcool.hystorms.repository.encyclopedia.EncyclopediaRepositoryImpl
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class PlantViewModel @Inject constructor(
    private val repository: EncyclopediaRepository
) : ViewModel() {

    fun doneShowingMessage() {
        _snackBarMessage.value = null
    }

    private val _viewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.LOADING)
    val viewState: LiveData<ViewState> = _viewState

    private val _snackBarMessage: MutableLiveData<String> = MutableLiveData()
    val snackBarMessage: LiveData<String> = _snackBarMessage

    private val _plants: MutableLiveData<List<Plant>> = MutableLiveData()
    val plants: LiveData<List<Plant>> = _plants

    init {
        viewModelScope.launch {
            repository.getPlants().onEach { dataState ->
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
                        _plants.value = dataState.data
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
package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.model.Plant
import org.d3ifcool.hystorms.model.Schedule
import org.d3ifcool.hystorms.model.Tank
import org.d3ifcool.hystorms.repository.tank.DetailTankRepositoryImpl
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class DetailTankViewModel @Inject constructor(
    private val repository: DetailTankRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _tank: MutableLiveData<Tank> = savedStateHandle.getLiveData("tank")
    val tank: LiveData<Tank> = _tank

    private val _plantViewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.LOADING)
    val plantViewState: LiveData<ViewState> = _plantViewState

    private val _scheduleViewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.LOADING)
    val scheduleViewState: LiveData<ViewState> = _scheduleViewState

    private val _snackBarMessage: MutableLiveData<String> = MutableLiveData()
    val snackBarMessage: LiveData<String> = _snackBarMessage
    fun doneShowingMessage() {
        _snackBarMessage.value = null
    }

    private val _plant: MutableLiveData<Plant> = MutableLiveData()
    val plant: LiveData<Plant> = _plant

    private val _schedule: MutableLiveData<List<Schedule>> = MutableLiveData()
    val schedule: LiveData<List<Schedule>> = _schedule

    fun getPlant(plantId: String) {
        viewModelScope.launch {
            repository.getPlant(plantId).onEach { state ->
                when (state) {
                    is DataState.Canceled -> {
                        _snackBarMessage.value = state.exception.message
                        _plantViewState.value = ViewState.ERROR
                    }
                    is DataState.Error -> {
                        _snackBarMessage.value = state.exception.message
                        _plantViewState.value = ViewState.ERROR
                    }
                    is DataState.Loading -> _plantViewState.value = ViewState.LOADING
                    is DataState.Success -> {
                        _plant.value = state.data
                        _plantViewState.value = ViewState.SUCCESS
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getSchedule(tankId: String, day: Int) {
        viewModelScope.launch {
            repository.getSchedules(tankId, day).onEach { state ->
                when (state) {
                    is DataState.Canceled -> {
                        _snackBarMessage.value = state.exception.message
                        _scheduleViewState.value = ViewState.ERROR
                    }
                    is DataState.Error -> {
                        _snackBarMessage.value = state.exception.message
                        _scheduleViewState.value = ViewState.ERROR
                    }
                    is DataState.Loading -> _scheduleViewState.value = ViewState.LOADING
                    is DataState.Success -> {
                        _schedule.value = state.data
                        _scheduleViewState.value = ViewState.SUCCESS
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}
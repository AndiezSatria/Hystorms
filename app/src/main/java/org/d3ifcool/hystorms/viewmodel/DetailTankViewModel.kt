package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.model.Plant
import org.d3ifcool.hystorms.model.Schedule
import org.d3ifcool.hystorms.model.Tank
import org.d3ifcool.hystorms.repository.tank.DetailTankRepository
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class DetailTankViewModel @Inject constructor(
    private val repository: DetailTankRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _tank: MutableLiveData<Tank> = MutableLiveData()
    val tank: LiveData<Tank> = _tank

    private val _tankId: MutableLiveData<String> = savedStateHandle.getLiveData("tankId")
    val tankId: LiveData<String> = _tankId

    private val _uid: MutableLiveData<String> = savedStateHandle.getLiveData("uid")
    val uid: LiveData<String> = _uid

    private val _plantViewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.LOADING)
    val plantViewState: LiveData<ViewState> = _plantViewState

    private val _isScheduleEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    val isScheduleEmpty: LiveData<Boolean>
        get() = _isScheduleEmpty

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
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackBarMessage.value = state.exception.message
                        _plantViewState.value = ViewState.ERROR
                    }
                    is DataState.Error -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackBarMessage.value = state.exception.message
                        _plantViewState.value = ViewState.ERROR
                    }
                    is DataState.Loading -> {
                        EspressoIdlingResource.increment()
                        _plantViewState.value = ViewState.LOADING
                    }
                    is DataState.Success -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _plant.value = state.data
                        _plantViewState.value = ViewState.SUCCESS
                    }
                    is DataState.ErrorThrowable -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackBarMessage.value = state.throwable.message
                        _plantViewState.value = ViewState.ERROR
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getTank(tankId: String) {
        viewModelScope.launch {
            repository.getTank(tankId).onEach { state ->
                when (state) {
                    is DataState.Canceled -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackBarMessage.value = state.exception.message
                        _plantViewState.value = ViewState.ERROR
                    }
                    is DataState.Error -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackBarMessage.value = state.exception.message
                        _plantViewState.value = ViewState.ERROR
                    }
                    is DataState.Loading -> {
                        EspressoIdlingResource.increment()
                        _plantViewState.value = ViewState.LOADING
                    }
                    is DataState.Success -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _tank.value = state.data
                    }
                    is DataState.ErrorThrowable -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackBarMessage.value = state.throwable.message
                        _plantViewState.value = ViewState.ERROR
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
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackBarMessage.value = state.exception.message
                        _scheduleViewState.value = ViewState.ERROR
                    }
                    is DataState.Error -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackBarMessage.value = state.exception.message
                        _scheduleViewState.value = ViewState.ERROR
                    }
                    is DataState.Loading -> {
                        EspressoIdlingResource.increment()
                        _scheduleViewState.value = ViewState.LOADING
                    }
                    is DataState.Success -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _schedule.value = state.data
                        _isScheduleEmpty.value = state.data.isEmpty()
                        _scheduleViewState.value = ViewState.SUCCESS
                    }
                    is DataState.ErrorThrowable -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackBarMessage.value = state.throwable.message
                        _scheduleViewState.value = ViewState.ERROR
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun deleteSchedule(listOfId: List<Long>, selectedDay: Int) {
        viewModelScope.launch {
            repository.deleteSchedule(listOfId).onEach { dataState ->
                when (dataState) {
                    is DataState.Canceled -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackBarMessage.value = dataState.exception.message
                        _scheduleViewState.value = ViewState.ERROR
                    }
                    is DataState.Error -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackBarMessage.value = dataState.exception.message
                        _scheduleViewState.value = ViewState.ERROR
                    }
                    is DataState.Loading -> {
                        EspressoIdlingResource.increment()
                        _scheduleViewState.value = ViewState.LOADING
                    }
                    is DataState.Success -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _scheduleViewState.value = ViewState.SUCCESS
                        getSchedule(_tank.value!!.id, selectedDay)
                        _snackBarMessage.value = dataState.data
                    }
                    is DataState.ErrorThrowable -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackBarMessage.value = dataState.throwable.message
                        _scheduleViewState.value = ViewState.ERROR
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}
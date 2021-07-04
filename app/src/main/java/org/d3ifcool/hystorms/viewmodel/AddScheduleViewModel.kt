package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.model.Nutrition
import org.d3ifcool.hystorms.model.Schedule
import org.d3ifcool.hystorms.repository.tank.AddScheduleRepository
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class AddScheduleViewModel @Inject constructor(
    private val repository: AddScheduleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _viewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.NOTHING)
    val viewState: LiveData<ViewState> = _viewState

    private val _uid: MutableLiveData<String> = savedStateHandle.getLiveData("uid")
    val uid: LiveData<String> = _uid

    private val _tankId: MutableLiveData<String> = savedStateHandle.getLiveData("tankId")
    val tankId: LiveData<String> = _tankId

    private val _scheduleEdit: MutableLiveData<Schedule> =
        savedStateHandle.getLiveData("schedule")
    val scheduleEdit: LiveData<Schedule> = _scheduleEdit

    private val _isEdit: MutableLiveData<Boolean> =
        MutableLiveData(savedStateHandle.get<Nutrition>("schedule") != null)
    val isEdit: LiveData<Boolean> = _isEdit

    private val _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> = _message

    private val _messageUpdate: MutableLiveData<String> = MutableLiveData()
    val messageUpdate: LiveData<String> = _messageUpdate

    fun doneShowMessage() {
        _message.value = null
    }

    fun doneShowMessageUpdate() {
        _messageUpdate.value = null
    }

    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch {
            repository.addSchedule(schedule).onEach { dataState ->
                when (dataState) {
                    is DataState.Canceled -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _message.value = dataState.exception.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.Error -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _message.value = dataState.exception.message
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
                        _messageUpdate.value = dataState.data
                        _viewState.value = ViewState.SUCCESS
                    }
                    is DataState.ErrorThrowable -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _message.value = dataState.throwable.message
                        _viewState.value = ViewState.ERROR
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            repository.updateSchedule(schedule).onEach { dataState ->
                when (dataState) {
                    is DataState.Canceled -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _message.value = dataState.exception.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.Error -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _message.value = dataState.exception.message
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
                        _messageUpdate.value = dataState.data
                        _viewState.value = ViewState.SUCCESS
                    }
                    is DataState.ErrorThrowable -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _message.value = dataState.throwable.message
                        _viewState.value = ViewState.ERROR
                    }
                }
            }.launchIn(viewModelScope)
        }
    }


}
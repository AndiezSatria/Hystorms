package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.model.*
import org.d3ifcool.hystorms.repository.auth.AuthenticationRepository
import org.d3ifcool.hystorms.repository.home.HomeRepository
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepositoryImpl: HomeRepository,
    private val authenticationRepositoryImpl: AuthenticationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User>
        get() = _user

    fun getUserState(uid: String) {
        viewModelScope.launch {
            authenticationRepositoryImpl.getUser(uid).onEach { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        _user.value = dataState.data
                    }
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private val _uid: MutableLiveData<String> = savedStateHandle.getLiveData(Constant.USER)
    val uid: LiveData<String> = _uid

    private val _device: MutableLiveData<Device> = MutableLiveData()
    val device: LiveData<Device> = _device

    fun getDevice(deviceId: String) {
        viewModelScope.launch {
            homeRepositoryImpl.getDevice(deviceId).onEach { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        _device.value = dataState.data
                    }
                    else -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private val _isTankEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    val isTankEmpty: LiveData<Boolean>
        get() = _isTankEmpty

    fun setIsTankEmpty(boolean: Boolean) {
        _isTankEmpty.value = boolean
    }

    private val _isScheduleEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    val isScheduleEmpty: LiveData<Boolean>
        get() = _isScheduleEmpty

    private val _weatherViewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.NOTHING)
    val weatherViewState: LiveData<ViewState>
        get() = _weatherViewState

    fun setWeatherViewState(viewState: ViewState) {
        _weatherViewState.value = viewState
    }


    private val _weather: MutableLiveData<DataState<Weather>> = MutableLiveData()
    val weather: LiveData<DataState<Weather>>
        get() = _weather

    fun getWeather(lat: Double, long: Double, language: String) {
        viewModelScope.launch {
            homeRepositoryImpl.getWeather(lat, long, language).onEach {
                _weather.value = it
            }.launchIn(viewModelScope)
        }
    }

    private val _tankViewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.NOTHING)
    val tankViewState: LiveData<ViewState>
        get() = _tankViewState

    fun setTankViewState(viewState: ViewState) {
        _tankViewState.value = viewState
    }

    private val _tanksDataState: MutableLiveData<DataState<List<Tank>>> = MutableLiveData()
    val tanksDataState: LiveData<DataState<List<Tank>>> = _tanksDataState

    fun getTanks(userId: String) {
        viewModelScope.launch {
            homeRepositoryImpl.getTanks(userId).onEach { dataState ->
                _tanksDataState.value = dataState
            }.launchIn(viewModelScope)
        }
    }

    private val _scheduleViewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.NOTHING)
    val scheduleViewState: LiveData<ViewState>
        get() = _scheduleViewState

    fun setScheduleViewState(viewState: ViewState) {
        _scheduleViewState.value = viewState
    }

    private val _schedulesDataState: MutableLiveData<DataState<List<Schedule>>> = MutableLiveData()
    val schedulesDataState: LiveData<DataState<List<Schedule>>> = _schedulesDataState

    fun getSchedule(userId: String, day: Int) {
        viewModelScope.launch {
            homeRepositoryImpl.getSchedules(userId, day).onEach { dataState ->
                _schedulesDataState.value = dataState
            }.launchIn(viewModelScope)
        }
    }
}
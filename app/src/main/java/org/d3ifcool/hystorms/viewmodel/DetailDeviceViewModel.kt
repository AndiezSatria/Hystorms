package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.model.*
import org.d3ifcool.hystorms.repository.device.DetailDeviceRepository
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class DetailDeviceViewModel @Inject constructor(
    private val repositoryImpl: DetailDeviceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isFavorite: MutableLiveData<Boolean> = MutableLiveData(
        savedStateHandle.get<User>("user")!!.favoriteDevice != null &&
                (savedStateHandle.get<User>("user")!!.favoriteDevice ==
                        savedStateHandle.get<Device>("device")!!.id)
    )
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _device: MutableLiveData<Device> = savedStateHandle.getLiveData("device")
    val device: LiveData<Device>
        get() = _device

    private val _user: MutableLiveData<User> = savedStateHandle.getLiveData("user")
    val user: LiveData<User>
        get() = _user

    private val _conditionState: MutableLiveData<DataState<Condition>> = MutableLiveData()
    val conditionState: LiveData<DataState<Condition>>
        get() = _conditionState

    private val _condition: MutableLiveData<List<SensorPhysic>> = MutableLiveData()
    val condition: LiveData<List<SensorPhysic>>
        get() = _condition

    fun setCondition(conditionGet: List<SensorPhysic>) {
        _condition.value = conditionGet
    }

    private val _tanksState: MutableLiveData<DataState<List<Tank>>> = MutableLiveData()
    val tanksState: LiveData<DataState<List<Tank>>>
        get() = _tanksState

    private val _detailViewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.LOADING)
    val detailViewState: LiveData<ViewState>
        get() = _detailViewState

    fun setDetailViewState(viewState: ViewState) = viewState.let { _detailViewState.value = it }

    private val _tanksViewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.LOADING)
    val tanksViewState: LiveData<ViewState>
        get() = _tanksViewState

    fun setTankViewState(viewState: ViewState) = viewState.let { _tanksViewState.value = it }

    private val _snackbarMessage: MutableLiveData<String> = MutableLiveData()
    val snackbarMessage: LiveData<String> = _snackbarMessage
    fun doneShowingSnackbar() {
        _snackbarMessage.value = null
    }

    private val _myAddress: MutableLiveData<MyAddress> = MutableLiveData()
    val myAddress: LiveData<MyAddress> = _myAddress
    fun setMyAddress(address: MyAddress?) {
        _myAddress.value = address
    }

    private val _weatherViewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.NOTHING)
    val weatherViewState: LiveData<ViewState>
        get() = _weatherViewState


    private val _weather: MutableLiveData<Weather> = MutableLiveData()
    val weather: LiveData<Weather>
        get() = _weather

    fun getWeather(lat: Double, long: Double, language: String) {
        viewModelScope.launch {
            repositoryImpl.getWeather(lat, long, language).onEach {
                when(it) {
                    is DataState.Canceled -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackbarMessage.value = it.exception.message
                        _weatherViewState.value = ViewState.ERROR
                    }
                    is DataState.Error -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackbarMessage.value = it.exception.message
                        _weatherViewState.value = ViewState.ERROR
                    }
                    is DataState.Loading -> {
                        EspressoIdlingResource.increment()
                        _weatherViewState.value = ViewState.LOADING
                    }
                    is DataState.Success -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _weatherViewState.value = ViewState.SUCCESS
                        _weather.value = it.data
                    }
                    is DataState.ErrorThrowable -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackbarMessage.value = it.throwable.message
                        _weatherViewState.value = ViewState.ERROR
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun setFavorite(deviceId: String?, userId: String) {
        viewModelScope.launch {
            repositoryImpl.setFavorite(deviceId, userId).onEach { dataState ->
                when (dataState) {
                    is DataState.Canceled -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackbarMessage.value = dataState.exception.message
                    }
                    is DataState.Error -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackbarMessage.value = dataState.exception.message
                    }
                    is DataState.Loading -> {
                        EspressoIdlingResource.increment()
                    }
                    is DataState.Success -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackbarMessage.value = dataState.data
                        _isFavorite.value = deviceId != null
                    }
                    is DataState.ErrorThrowable -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackbarMessage.value = dataState.throwable.message
                        _detailViewState.value = ViewState.SUCCESS
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun updateLocation(address: MyAddress, device: Device) {
        viewModelScope.launch {
            repositoryImpl.updateLocation(address, device).onEach {
                when (it) {
                    is DataState.Canceled -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackbarMessage.value = it.exception.message
                        _detailViewState.value = ViewState.SUCCESS
                    }
                    is DataState.Error -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackbarMessage.value = it.exception.message
                        _detailViewState.value = ViewState.SUCCESS
                    }
                    is DataState.Loading -> {
                        EspressoIdlingResource.increment()
                        _detailViewState.value = ViewState.LOADING
                    }
                    is DataState.Success -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackbarMessage.value = "Berhasil mengubah lokasi"
                        _detailViewState.value = ViewState.SUCCESS
                        _device.value = it.data
                    }
                    is DataState.ErrorThrowable -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _snackbarMessage.value = it.throwable.message
                        _detailViewState.value = ViewState.SUCCESS
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getCondition(conditionId: String) {
        viewModelScope.launch {
            repositoryImpl.getCondition(conditionId).onEach {
                _conditionState.value = it
            }.launchIn(viewModelScope)
        }
    }

    fun getTank(deviceId: String) {
        viewModelScope.launch {
            repositoryImpl.getTanks(deviceId).onEach {
                _tanksState.value = it
            }.launchIn(viewModelScope)
        }
    }
}
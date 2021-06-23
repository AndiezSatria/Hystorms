package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.model.AddDevice
import org.d3ifcool.hystorms.model.Device
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.repository.auth.AuthenticationRepository
import org.d3ifcool.hystorms.repository.device.DevicesRepository
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val devicesRepositoryImpl: DevicesRepository,
    private val authenticationRepositoryImpl: AuthenticationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _viewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.LOADING)
    val viewState: LiveData<ViewState>
        get() = _viewState

    fun setViewState(viewState: ViewState) {
        _viewState.value = viewState
    }

    private val _isEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty

    fun setIsEmpty(boolean: Boolean) {
        _isEmpty.value = boolean
    }

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User>
        get() = _user

    fun getUserState(uid: String) {
        viewModelScope.launch {
            authenticationRepositoryImpl.getUser(uid).onEach { dataState ->
                when (dataState) {
                    is DataState.Canceled -> {
                        EspressoIdlingResource.decrement()
                        setViewState(ViewState.ERROR)
                    }
                    is DataState.Error -> {
                        EspressoIdlingResource.decrement()
                        setViewState(ViewState.ERROR)
                    }
                    is DataState.Loading -> {
                        EspressoIdlingResource.increment()
                        setViewState(ViewState.LOADING)
                    }
                    is DataState.Success -> {
                        EspressoIdlingResource.decrement()
                        _user.value = dataState.data
                    }
                    is DataState.ErrorThrowable -> {
                        EspressoIdlingResource.decrement()
                        setViewState(ViewState.ERROR)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private val _device: MutableLiveData<Device> = MutableLiveData()
    val device: LiveData<Device> = _device

    private val _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> = _message
    fun doneShowMessage() {
        _message.value = null
    }

    private val _messageUpdate: MutableLiveData<String> = MutableLiveData()
    val messageUpdate: LiveData<String> = _messageUpdate
    fun doneShowMessageUpdate() {
        _messageUpdate.value = null
    }

    private val _uid: MutableLiveData<String> = savedStateHandle.getLiveData(Constant.USER)
    val uid: LiveData<String> = _uid

    private val _devices: MutableLiveData<DataState<List<Device>>> = MutableLiveData()
    val devices: LiveData<DataState<List<Device>>>
        get() = _devices

    fun doneUpdateDevice() {
        _device.value = null
    }

    fun getDevice(addDevice: AddDevice) {
        viewModelScope.launch {
            devicesRepositoryImpl.getDevice(addDevice).onEach { dataState ->
                when (dataState) {
                    is DataState.Canceled -> {
                        EspressoIdlingResource.decrement()
                        _message.value = dataState.exception.message
                    }
                    is DataState.Error -> {
                        EspressoIdlingResource.decrement()
                        _message.value =
                            if (dataState.exception is IndexOutOfBoundsException) "Alat tidak ditemukan."
                            else dataState.exception.message
                    }
                    is DataState.Loading -> {
                        EspressoIdlingResource.increment()
                    }
                    is DataState.Success -> {
                        EspressoIdlingResource.decrement()
                        _device.value = dataState.data
                    }
                    is DataState.ErrorThrowable -> {
                        EspressoIdlingResource.decrement()
                        _message.value = dataState.throwable.message
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun updateDevice(uid: String, deviceId: String) {
        viewModelScope.launch {
            devicesRepositoryImpl.updateOwner(uid, deviceId).onEach { dataState ->
                when (dataState) {
                    is DataState.Canceled -> {
                        EspressoIdlingResource.decrement()
                        _message.value = dataState.exception.message
                    }
                    is DataState.Error -> {
                        EspressoIdlingResource.decrement()
                        _message.value = dataState.exception.message
                    }
                    is DataState.Loading -> {
                        EspressoIdlingResource.increment()
                    }
                    is DataState.Success -> {
                        EspressoIdlingResource.decrement()
                        _messageUpdate.value = dataState.data
                    }
                    is DataState.ErrorThrowable -> {
                        EspressoIdlingResource.decrement()
                        _message.value = dataState.throwable.message
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun deleteOwnership(listOfIdDevice: List<String>) {
        viewModelScope.launch {
            devicesRepositoryImpl.deleteOwnership(listOfIdDevice).onEach { dataState ->
                when (dataState) {
                    is DataState.Canceled -> {
                        EspressoIdlingResource.decrement()
                        _message.value = dataState.exception.message
                    }
                    is DataState.Error -> {
                        EspressoIdlingResource.decrement()
                        _message.value = dataState.exception.message
                    }
                    is DataState.Loading -> {
                        EspressoIdlingResource.increment()
                    }
                    is DataState.Success -> {
                        EspressoIdlingResource.decrement()
                        _messageUpdate.value = dataState.data
                        getDevices(_user.value!!)
                    }
                    is DataState.ErrorThrowable -> {
                        EspressoIdlingResource.decrement()
                        _message.value = dataState.throwable.message
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getDevices(user: User) {
        viewModelScope.launch {
            devicesRepositoryImpl.getDevices(user).onEach { state ->
                _devices.value = state
            }.launchIn(viewModelScope)
        }
    }
}
package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.model.Device
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.repository.auth.AuthenticationRepositoryImpl
import org.d3ifcool.hystorms.repository.device.DevicesRepositoryImpl
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val devicesRepositoryImpl: DevicesRepositoryImpl,
    private val authenticationRepositoryImpl: AuthenticationRepositoryImpl,
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
                        setViewState(ViewState.ERROR)
                    }
                    is DataState.Error -> {
                        setViewState(ViewState.ERROR)
                    }
                    is DataState.Loading -> {
                        setViewState(ViewState.LOADING)
                    }
                    is DataState.Success -> {
                        _user.value = dataState.data
                    }
                }
            }.launchIn(viewModelScope)
        }
    }


    private val _uid: MutableLiveData<String> = savedStateHandle.getLiveData(Constant.USER)
    val uid: LiveData<String> = _uid

    private val _devices: MutableLiveData<DataState<List<Device>>> = MutableLiveData()
    val devices: LiveData<DataState<List<Device>>>
        get() = _devices

    fun getDevices(user: User) {
        viewModelScope.launch {
            devicesRepositoryImpl.getDevices(user).onEach { state ->
                _devices.value = state
            }.launchIn(viewModelScope)
        }
    }
}
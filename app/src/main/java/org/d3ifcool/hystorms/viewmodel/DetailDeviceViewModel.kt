package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.model.Condition
import org.d3ifcool.hystorms.model.Device
import org.d3ifcool.hystorms.model.Tank
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.repository.device.DetailDeviceRepositoryImpl
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class DetailDeviceViewModel @Inject constructor(
    private val repositoryImpl: DetailDeviceRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isFavorite: MutableLiveData<Boolean> = MutableLiveData(
        savedStateHandle.get<User>("user")!!.favoriteDevice != null &&
                (savedStateHandle.get<User>("user")!!.favoriteDevice ==
                        savedStateHandle.get<Device>("device")!!.id)
    )
    val isFavorite: LiveData<Boolean> = _isFavorite
    fun setIsFavorite(boolean: Boolean) = boolean.let { _isFavorite.value = it }

    private val _device: MutableLiveData<Device> = savedStateHandle.getLiveData("device")
    val device: LiveData<Device>
        get() = _device

    private val _user: MutableLiveData<User> = savedStateHandle.getLiveData("user")
    val user: LiveData<User>
        get() = _user

    private val _conditionState: MutableLiveData<DataState<Condition>> = MutableLiveData()
    val conditionState: LiveData<DataState<Condition>>
        get() = _conditionState

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

    fun setFavorite(deviceId: String?, userId: String) {
        viewModelScope.launch {
            repositoryImpl.setFavorite(deviceId, userId).onEach { dataState ->
                when (dataState) {
                    is DataState.Canceled -> _snackbarMessage.value = dataState.exception.message
                    is DataState.Error -> _snackbarMessage.value = dataState.exception.message
                    is DataState.Loading -> {
                    }
                    is DataState.Success -> {
                        _snackbarMessage.value = dataState.data
                        _isFavorite.value = deviceId != null
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
package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.repository.setting.SettingRepositoryImpl
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val repository: SettingRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uid: MutableLiveData<String> = savedStateHandle.getLiveData("uid")
    val uid: LiveData<String> = _uid

    private val _viewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.NOTHING)
    val viewState: LiveData<ViewState> = _viewState

    private val _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> = _message
    fun doneShowMessage() {
        _message.value = null
    }

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> = _user

    fun getUser(uid: String) {
        viewModelScope.launch {
            repository.getUser(uid).onEach { state ->
                when (state) {
                    is DataState.Canceled -> {
                        _message.value = state.exception.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.Error -> {
                        _message.value = state.exception.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.Loading -> _viewState.value = ViewState.LOADING
                    is DataState.Success -> {
                        _user.value = state.data
                        _viewState.value = ViewState.SUCCESS
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}
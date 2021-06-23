package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.repository.setting.SettingRepository
import org.d3ifcool.hystorms.repository.setting.SettingRepositoryImpl
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val repository: SettingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uid: MutableLiveData<String> = savedStateHandle.getLiveData(Constant.USER)
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

    private val _isLoggedOut: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoggedOut: LiveData<Boolean> = _isLoggedOut

    fun signOut() {
        viewModelScope.launch {
            repository.logout()
            _isLoggedOut.value = true
        }
    }

    fun getUser(uid: String) {
        viewModelScope.launch {
            repository.getUser(uid).onEach { state ->
                when (state) {
                    is DataState.Canceled -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _message.value = state.exception.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.Error -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _message.value = state.exception.message
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
                        _user.value = state.data
                        _viewState.value = ViewState.SUCCESS
                    }
                    is DataState.ErrorThrowable -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _message.value = state.throwable.message
                        _viewState.value = ViewState.ERROR
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun doneLoggedOut() {
        _isLoggedOut.value = false
    }
}
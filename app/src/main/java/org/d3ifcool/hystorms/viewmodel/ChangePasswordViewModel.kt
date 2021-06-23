package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.repository.setting.ChangePasswordRepository
import org.d3ifcool.hystorms.repository.setting.ChangePasswordRepositoryImpl
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val repository: ChangePasswordRepository
) : ViewModel() {
    private val _viewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.NOTHING)
    val viewState: LiveData<ViewState> = _viewState

    private val _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> = _message

    private val _messageUpdate: MutableLiveData<String> = MutableLiveData()
    val messageUpdate: LiveData<String> = _messageUpdate

    fun changePassword(pass: String) {
        viewModelScope.launch {
            repository.changePassword(pass).onEach { state ->
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
                        _messageUpdate.value = state.data
                        _viewState.value = ViewState.SUCCESS
                    }
                    is DataState.ErrorThrowable -> {
                        _message.value = state.throwable.message
                        _viewState.value = ViewState.ERROR
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun doneShowMessage() {
        _message.value = null
    }
    fun doneShowMessageUpdate() {
        _messageUpdate.value = null
    }
}
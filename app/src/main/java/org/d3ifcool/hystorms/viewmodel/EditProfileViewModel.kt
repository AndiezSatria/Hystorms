package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.repository.setting.EditProfileRepository
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.ButtonUploadState
import org.d3ifcool.hystorms.util.ViewState
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: EditProfileRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _user: MutableLiveData<User> = savedStateHandle.getLiveData("user")
    val user: LiveData<User> = _user

    private val _viewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.NOTHING)
    val viewState: LiveData<ViewState> = _viewState

    private val _buttonState: MutableLiveData<ButtonUploadState> =
        MutableLiveData(ButtonUploadState.ADD)
    val buttonState: LiveData<ButtonUploadState> = _buttonState
    fun setButtonState(buttonUploadState: ButtonUploadState) {
        _buttonState.value = buttonUploadState
    }

    private val _url: MutableLiveData<String> = MutableLiveData()
    val url: LiveData<String> = _url
    fun setUrl(urlString: String?) {
        _url.value = urlString
        if (urlString != null) setButtonState(ButtonUploadState.DELETE)
        else setButtonState(ButtonUploadState.ADD)
    }

    private val _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> = _message

    private val _messageUpdate: MutableLiveData<String> = MutableLiveData()
    val messageUpdate: LiveData<String> = _messageUpdate

    private val _uploadedFileUser: MutableLiveData<User> = MutableLiveData()
    val uploadedFileUser: LiveData<User> = _uploadedFileUser

    private val _fileToUpload: MutableLiveData<File> = MutableLiveData()
    val fileToUpload: LiveData<File> = _fileToUpload
    fun setFile(file: File?) {
        _fileToUpload.value = file
        if (file != null) setButtonState(ButtonUploadState.DELETE)
        else setButtonState(ButtonUploadState.ADD)
    }

    fun uploadImage(user: User, file: File) {
        viewModelScope.launch {
            repository.uploadImageAndGetUrl(user, file).onEach { state ->
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
                        _uploadedFileUser.value = state.data
                    }
                    is DataState.ErrorThrowable -> {
                        _message.value = state.throwable.message
                        _viewState.value = ViewState.ERROR
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUser(user).onEach { state ->
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
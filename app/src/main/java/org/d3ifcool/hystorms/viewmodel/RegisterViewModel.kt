package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.model.DataOrException
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.repository.AuthRepository
import org.d3ifcool.hystorms.util.ButtonUploadState
import org.d3ifcool.hystorms.util.ViewState
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _viewState: MutableLiveData<ViewState> = authRepository.viewState
    val viewState: LiveData<ViewState>
        get() = _viewState

    private val _buttonState: MutableLiveData<ButtonUploadState> = MutableLiveData()
    val buttonState: LiveData<ButtonUploadState>
        get() = _buttonState

    private val _fileToUpload: MutableLiveData<File> = MutableLiveData()
    val fileToUpload: LiveData<File>
        get() = _fileToUpload

    private val _dataRegister: MutableLiveData<DataOrException<User, Exception>> = MutableLiveData()
    val dataRegister: LiveData<DataOrException<User, Exception>>
        get() = _dataRegister

    fun setFile(file: File) {
        _fileToUpload.value = file
    }

    fun deleteFile() {
        _fileToUpload.value = null
    }

    fun setButtonState(buttonUploadState: ButtonUploadState) {
        _buttonState.value = buttonUploadState
    }

    fun deleteData() {
        _dataRegister.value = null
    }

    fun register(user: User, pass: String, file: File?) {
        viewModelScope.launch {
            _dataRegister.value = authRepository.firebaseRegister(user, pass, file)
        }
    }

    init {
        setButtonState(ButtonUploadState.ADD)
    }
}
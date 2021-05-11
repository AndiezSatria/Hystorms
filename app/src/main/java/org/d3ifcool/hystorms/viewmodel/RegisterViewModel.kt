package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.*
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
    private val authRepository: AuthRepository
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

    private val _authenticatedUser: MutableLiveData<DataOrException<User, Exception>> =
        authRepository.authenticatedUser
    val authenticatedUser: LiveData<DataOrException<User, Exception>>
        get() = _authenticatedUser

    private val _profileUploadedUser: MutableLiveData<DataOrException<User, Exception>> =
        authRepository.profileUploadedUser
    val profileUploadedUser: LiveData<DataOrException<User, Exception>>
        get() = _profileUploadedUser

    private val _savedUser: MutableLiveData<DataOrException<User, Exception>> =
        authRepository.savedUser
    val savedUser: LiveData<DataOrException<User, Exception>>
        get() = _savedUser

    fun setFile(file: File) {
        _fileToUpload.value = file
    }

    fun deleteFile() {
        _fileToUpload.value = null
    }

    fun setButtonState(buttonUploadState: ButtonUploadState) {
        _buttonState.value = buttonUploadState
    }

    fun resetData() {
        deleteFile()
        authRepository.resetState()
        authRepository.resetData()
    }

    fun registerAuth(user: User, pass: String) {
        viewModelScope.launch {
            authRepository.firebaseRegister(user, pass)
        }
    }

    fun uploadProfile(user: User, file: File) {
        viewModelScope.launch {
            authRepository.firebaseUploadProfilePicture(file, user)
        }
    }

    fun saveUser(user: User) {
        viewModelScope.launch {
            authRepository.setUser(user)
        }
    }

    init {
        setButtonState(ButtonUploadState.ADD)
    }
}
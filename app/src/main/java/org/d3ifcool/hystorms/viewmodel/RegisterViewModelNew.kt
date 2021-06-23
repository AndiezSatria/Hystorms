package org.d3ifcool.hystorms.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.repository.auth.AuthenticationRepository
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.ButtonUploadState
import org.d3ifcool.hystorms.util.ViewState
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegisterViewModelNew @Inject constructor(
    private val authRepo: AuthenticationRepository
) : ViewModel() {
    init {
        checkToken()
    }

    private fun checkToken() {
        val tokenTask =
            FirebaseMessaging.getInstance().token
        tokenTask.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "FCM token failed.", task.exception)
                return@OnCompleteListener
            }
            Action.showLog(task.result)
            _token.value = task.result
        })
    }

    private val _token: MutableLiveData<String> = MutableLiveData()
    val token: LiveData<String>
        get() = _token

    private val _viewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.NOTHING)
    val viewState: LiveData<ViewState>
        get() = _viewState

    private val _buttonState: MutableLiveData<ButtonUploadState> =
        MutableLiveData(ButtonUploadState.ADD)
    val buttonState: LiveData<ButtonUploadState>
        get() = _buttonState

    private val _fileToUpload: MutableLiveData<File> = MutableLiveData()
    val fileToUpload: LiveData<File>
        get() = _fileToUpload

    private val _authenticatedUser: MutableLiveData<DataState<User>> =
        MutableLiveData()
    val authenticatedUser: LiveData<DataState<User>>
        get() = _authenticatedUser

    private val _profileUploadedUser: MutableLiveData<DataState<User>> =
        MutableLiveData()
    val profileUploadedUser: LiveData<DataState<User>>
        get() = _profileUploadedUser

    private val _savedUser: MutableLiveData<DataState<User>> =
        MutableLiveData()
    val savedUser: LiveData<DataState<User>>
        get() = _savedUser

    fun setFile(file: File) {
        _fileToUpload.value = file
    }

    fun deleteFile() {
        _fileToUpload.value = null
    }

    fun setViewState(viewState: ViewState) {
        viewModelScope.launch {
            _viewState.value = viewState
        }
    }

    fun setButtonState(buttonUploadState: ButtonUploadState) {
        viewModelScope.launch {
            _buttonState.value = buttonUploadState
        }
    }

    fun registerAuth(user: User, pass: String) {
        viewModelScope.launch {
            authRepo.signUpUser(user, pass).onEach { state ->
                _authenticatedUser.value = state
            }.launchIn(viewModelScope)
        }
    }

    fun uploadProfile(user: User, file: File) {
        viewModelScope.launch {
            authRepo.uploadImageToStorage(file, user).onEach { state ->
                _profileUploadedUser.value = state
            }.launchIn(viewModelScope)
        }
    }

    fun saveUser(user: User) {
        viewModelScope.launch {
            authRepo.createUserInFirestore(user).onEach { state ->
                _savedUser.value = state
            }.launchIn(viewModelScope)
        }
    }

    fun resetData() {
        _authenticatedUser.value = null
        _savedUser.value = null
        _profileUploadedUser.value = null
        setViewState(ViewState.NOTHING)
        setButtonState(ButtonUploadState.ADD)
        deleteFile()
    }
}
package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.model.DataOrException
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.repository.AuthRepository
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _viewState: MutableLiveData<ViewState> = authRepository.viewState
    val viewState: LiveData<ViewState>
        get() = _viewState

    private val _authenticatedUid: MutableLiveData<DataOrException<String, Exception>> =
        authRepository.authenticatedUid
    val authenticatedUid: LiveData<DataOrException<String, Exception>>
        get() = _authenticatedUid

    private val _loggedInUser: MutableLiveData<DataOrException<User, Exception>> =
        authRepository.loggedInUser
    val loggedInUser: LiveData<DataOrException<User, Exception>>
        get() = _loggedInUser

    fun signIn(email: String, pass: String) {
        viewModelScope.launch {
            authRepository.firebaseSignIn(email, pass)
        }
    }

    fun getUser(uid: String) {
        viewModelScope.launch {
            authRepository.getUser(uid)
        }
    }
}
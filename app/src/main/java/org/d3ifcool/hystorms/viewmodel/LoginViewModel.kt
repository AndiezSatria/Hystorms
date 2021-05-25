package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.repository.auth.AuthenticationRepositoryImpl
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthenticationRepositoryImpl
) : ViewModel() {
    private val _viewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.NOTHING)
    val viewState: LiveData<ViewState>
        get() = _viewState

    fun setState(viewState: ViewState) {
        _viewState.value = viewState
    }

    private val _authenticatedUid: MutableLiveData<DataState<String>> =
        MutableLiveData()
    val authenticatedUid: LiveData<DataState<String>>
        get() = _authenticatedUid

    private val _loggedInUser: MutableLiveData<DataState<User>> =
        MutableLiveData()
    val loggedInUser: LiveData<DataState<User>>
        get() = _loggedInUser

    fun signIn(email: String, pass: String) {
        viewModelScope.launch {
            authRepository.signInUser(email, pass).onEach { state ->
                _authenticatedUid.value = state
            }.launchIn(viewModelScope)
        }
    }

    fun getUser(uid: String) {
        viewModelScope.launch {
            authRepository.getUser(uid).onEach { state ->
                _loggedInUser.value = state
            }.launchIn(viewModelScope)
        }
    }
}
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
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthenticationRepository
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

    fun getUser(uid: String, token: String) {
        viewModelScope.launch {
            authRepository.getUserLogin(uid, token).onEach { state ->
                _loggedInUser.value = state
            }.launchIn(viewModelScope)
        }
    }
}
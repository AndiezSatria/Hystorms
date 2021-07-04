package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.repository.auth.AuthenticationRepository
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthenticationRepository
) : ViewModel() {
    private val _viewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.NOTHING)
    val viewState: LiveData<ViewState>
        get() = _viewState

    fun setState(viewState: ViewState) {
        _viewState.value = viewState
    }

    private val _resetPassResult: MutableLiveData<DataState<String>> =
        MutableLiveData()
    val resetPassResult: LiveData<DataState<String>>
        get() = _resetPassResult

    fun resetPass(email: String) {
        viewModelScope.launch {
            authRepository.resetPassword(email).onEach { state ->
                _resetPassResult.value = state
            }.launchIn(viewModelScope)
        }
    }
}
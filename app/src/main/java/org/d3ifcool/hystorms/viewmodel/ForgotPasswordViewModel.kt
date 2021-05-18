package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.model.DataOrException
import org.d3ifcool.hystorms.repository.AuthRepository
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _viewState: MutableLiveData<ViewState> = authRepository.viewState
    val viewState: LiveData<ViewState>
        get() = _viewState

    private val _resetPassResult: MutableLiveData<DataOrException<String, Exception>> =
        authRepository.resetPasswordResult
    val resetPassResult: LiveData<DataOrException<String, Exception>>
        get() = _resetPassResult

    fun resetPass(email: String) {
        viewModelScope.launch {
            authRepository.resetPassword(email)
        }
    }
}
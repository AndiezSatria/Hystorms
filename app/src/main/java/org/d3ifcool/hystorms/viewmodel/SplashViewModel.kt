package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.model.DataOrException
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.repository.splash.SplashRepositoryImpl
import org.d3ifcool.hystorms.state.DataState
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val splashRepositoryImpl: SplashRepositoryImpl
) : ViewModel() {
    private val _user: MutableLiveData<DataState<User>> = MutableLiveData()
    val user: LiveData<DataState<User>>
        get() = _user

    fun checkIfUserIsAuthenticated(): Boolean =
        splashRepositoryImpl.checkIfUserIsAuthenticatedInFirebase()

    fun getUid(): String? = splashRepositoryImpl.getFirebaseUid()

    fun getUser(uid: String) = viewModelScope.launch {
        splashRepositoryImpl.getUserDataFromFirestore(uid).onEach {
            _user.value = it
        }.launchIn(viewModelScope)
    }
}
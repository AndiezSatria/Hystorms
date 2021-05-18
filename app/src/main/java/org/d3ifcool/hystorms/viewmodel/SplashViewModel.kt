package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.model.DataOrException
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.repository.SplashRepository
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val splashRepository: SplashRepository
) : ViewModel() {
    private val _user: MutableLiveData<DataOrException<User, Exception>> = splashRepository.user
    val user: LiveData<DataOrException<User, Exception>>
        get() = _user

    fun checkIfUserIsAuthenticated(): Boolean =
        splashRepository.checkIfUserIsAuthenticatedInFirebase()

    fun getUid(): String? = splashRepository.getFirebaseUid()

    fun getUser(uid: String) = viewModelScope.launch {
        splashRepository.getUserDataFromFirestore(uid)
    }
}
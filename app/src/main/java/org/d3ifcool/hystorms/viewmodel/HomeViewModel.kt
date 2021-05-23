package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.model.DataOrException
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.model.Weather
import org.d3ifcool.hystorms.repository.HomeRepository
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _user: MutableLiveData<User> = savedStateHandle.getLiveData(Constant.USER)
    val user: LiveData<User>
        get() = _user

    private val _weatherViewState: MutableLiveData<ViewState> = homeRepository.weatherViewState
    val weatherViewState: LiveData<ViewState>
        get() = _weatherViewState

    private val _weather: MutableLiveData<DataOrException<Weather, Exception>> =
        homeRepository.weatherMutableLiveData
    val weather: LiveData<DataOrException<Weather, Exception>>
        get() = _weather

    init {
        viewModelScope.launch {
            homeRepository.getWeather(
                6.9175.toLong(),
                107.6191.toLong(),
                "id"
            )
        }
    }

    fun getWeather(lat: Long, long: Long, language: String) {
        viewModelScope.launch {
            homeRepository.getWeather(lat, long, language)
        }
    }
}
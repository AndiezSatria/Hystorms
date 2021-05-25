package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.model.DataOrException
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.model.Weather
import org.d3ifcool.hystorms.repository.home.HomeRepositoryImpl
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.ViewState
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepositoryImpl: HomeRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _user: MutableLiveData<User> = savedStateHandle.getLiveData(Constant.USER)
    val user: LiveData<User>
        get() = _user

    private val _weatherViewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.NOTHING)
    val weatherViewState: LiveData<ViewState>
        get() = _weatherViewState

    fun setWeatherViewState(viewState: ViewState) {
        _weatherViewState.value = viewState
    }

    private val _weather: MutableLiveData<DataState<Weather>> = MutableLiveData()
    val weather: LiveData<DataState<Weather>>
        get() = _weather

    init {
        viewModelScope.launch {
            homeRepositoryImpl.getWeather(
                6.9175,
                107.6191,
                "id"
            ).onEach {
                _weather.value = it
            }.launchIn(viewModelScope)
        }
    }

    fun getWeather(lat: Double, long: Double, language: String) {
        viewModelScope.launch {
            homeRepositoryImpl.getWeather(lat, long, language).onEach {
                _weather.value = it
            }.launchIn(viewModelScope)
        }
    }
}
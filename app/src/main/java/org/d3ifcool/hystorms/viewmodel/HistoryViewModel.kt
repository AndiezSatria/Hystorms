package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.*
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.model.History
import org.d3ifcool.hystorms.repository.tank.HistoryRepository
import org.d3ifcool.hystorms.repository.tank.HistoryRepositoryImpl
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.ViewState
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: HistoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _tankId: MutableLiveData<String> = savedStateHandle.getLiveData("tankId")
    val tankId: LiveData<String> = _tankId

    private val _viewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.LOADING)
    val viewState: LiveData<ViewState> = _viewState

    private val _isEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    val isEmpty: LiveData<Boolean> = _isEmpty
    fun setIsEmpty(boolean: Boolean) {
        _isEmpty.value = boolean
    }

    private val _date: MutableLiveData<Date> = MutableLiveData()
    val date: LiveData<Date> = _date
    fun setDate(datePicked: Date) {
        _date.value = datePicked
    }

    private val _history: MutableLiveData<List<History>> = MutableLiveData()
    val history: LiveData<List<History>> = _history

    private val _historyGraph: MutableLiveData<List<History>> = MutableLiveData()
    val historyGraph: LiveData<List<History>> = _historyGraph

    private val _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> = _message
    fun doneShowMessage() {
        _message.value = null
    }

    fun getHistory(tankId: String, dateStart: Date, dateEnd: Date) {
        viewModelScope.launch {
            repository.getHistory(tankId, dateStart, dateEnd).onEach { state ->
                when (state) {
                    is DataState.Canceled -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _message.value = state.exception.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.Error -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _message.value = state.exception.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.ErrorThrowable -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _message.value = state.throwable.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.Loading -> {
                        EspressoIdlingResource.increment()
                        _viewState.value = ViewState.LOADING
                    }
                    is DataState.Success -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _viewState.value = ViewState.SUCCESS
                        _history.value = state.data
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getHistoryGraph(tankId: String, dateNow: Date) {
        viewModelScope.launch {
            repository.getHistoryGraph(tankId, dateNow).onEach { state ->
                when (state) {
                    is DataState.Canceled -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _message.value = state.exception.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.Error -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _message.value = state.exception.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.ErrorThrowable -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _message.value = state.throwable.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.Loading -> {
                        EspressoIdlingResource.increment()
                        _viewState.value = ViewState.LOADING
                    }
                    is DataState.Success -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _viewState.value = ViewState.SUCCESS
                        _historyGraph.value = state.data
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}
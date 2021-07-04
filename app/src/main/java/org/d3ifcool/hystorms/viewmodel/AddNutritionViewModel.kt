package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.d3ifcool.hystorms.model.Nutrition
import org.d3ifcool.hystorms.repository.encyclopedia.AddNutritionRepository
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.ButtonUploadState
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.ViewState
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddNutritionViewModel @Inject constructor(
    private val repository: AddNutritionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _viewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.NOTHING)
    val viewState: LiveData<ViewState> = _viewState

    private val _uid: MutableLiveData<String> = savedStateHandle.getLiveData("uid")
    val uid: LiveData<String> = _uid

    private val _nutritionEdit: MutableLiveData<Nutrition> =
        savedStateHandle.getLiveData("nutrition")
    val nutritionEdit: LiveData<Nutrition> = _nutritionEdit

    private val _uploadedNutrition: MutableLiveData<Nutrition> =
        MutableLiveData()
    val uploadedNutrition: LiveData<Nutrition> = _uploadedNutrition
    fun doneSetNutrition() {
        _uploadedNutrition.value = null
    }

    private val _isEdit: MutableLiveData<Boolean> =
        MutableLiveData(savedStateHandle.get<Nutrition>("nutrition") != null)
    val isEdit: LiveData<Boolean> = _isEdit

    private val _buttonState: MutableLiveData<ButtonUploadState> =
        MutableLiveData(ButtonUploadState.ADD)
    val buttonState: LiveData<ButtonUploadState>
        get() = _buttonState

    fun setButtonState(buttonUploadState: ButtonUploadState) {
        _buttonState.value = buttonUploadState
    }

    private val _url: MutableLiveData<String> = MutableLiveData()
    val url: LiveData<String> = _url
    fun setUrl(urlString: String?) {
        _url.value = urlString
        if (urlString != null) setButtonState(ButtonUploadState.DELETE)
        else setButtonState(ButtonUploadState.ADD)
    }

    private val _fileToUpload: MutableLiveData<File> = MutableLiveData()
    val fileToUpload: LiveData<File>
        get() = _fileToUpload

    fun setFile(file: File?) {
        _fileToUpload.value = file
        if (file != null) setButtonState(ButtonUploadState.DELETE)
        else setButtonState(ButtonUploadState.ADD)
    }

    private val _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> = _message

    private val _messageUpdate: MutableLiveData<String> = MutableLiveData()
    val messageUpdate: LiveData<String> = _messageUpdate

    fun doneShowMessage() {
        _message.value = null
    }

    fun doneShowMessageUpdate() {
        _messageUpdate.value = null
    }

    fun uploadImage(file: File, nutrition: Nutrition) {
        viewModelScope.launch {
            repository.uploadImage(file, nutrition).onEach { state ->
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
                    is DataState.Loading -> {
                        EspressoIdlingResource.increment()
                        _viewState.value = ViewState.LOADING
                    }
                    is DataState.Success -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _uploadedNutrition.value = state.data
                    }
                    is DataState.ErrorThrowable -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _message.value = state.throwable.message
                        _viewState.value = ViewState.ERROR
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun addNutrition(nutrition: Nutrition) {
        viewModelScope.launch {
            repository.setNutrition(nutrition).onEach { state ->
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
                    is DataState.Loading -> {
                        EspressoIdlingResource.increment()
                        _viewState.value = ViewState.LOADING
                    }
                    is DataState.Success -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _messageUpdate.value = state.data
                        _viewState.value = ViewState.SUCCESS
                    }
                    is DataState.ErrorThrowable -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                            EspressoIdlingResource.decrement() // Set app as idle.
                        }
                        _message.value = state.throwable.message
                        _viewState.value = ViewState.ERROR
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun updateNutrition(nutrition: Nutrition) {
        viewModelScope.launch {
            repository.updateNutrition(nutrition).onEach { state ->
                when (state) {
                    is DataState.Canceled -> {
                        _message.value = state.exception.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.Error -> {
                        _message.value = state.exception.message
                        _viewState.value = ViewState.ERROR
                    }
                    is DataState.Loading -> _viewState.value = ViewState.LOADING
                    is DataState.Success -> {
                        _messageUpdate.value = state.data
                        _viewState.value = ViewState.SUCCESS
                    }
                    is DataState.ErrorThrowable -> {
                        _message.value = state.throwable.message
                        _viewState.value = ViewState.ERROR
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}
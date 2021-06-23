package org.d3ifcool.hystorms.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.d3ifcool.hystorms.model.Schedule
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _schedule: MutableLiveData<Schedule> = savedStateHandle.getLiveData("schedule")
    val schedule: LiveData<Schedule> = _schedule

    private val _selectedDay: MutableLiveData<Int> = savedStateHandle.getLiveData("daySelected")
    val selectedDay: LiveData<Int> = _selectedDay

    private val _isAlarmSet: MutableLiveData<Boolean> = MutableLiveData(false)
    val isAlarmSet: LiveData<Boolean> = _isAlarmSet
    fun setIsAlarmSet(boolean: Boolean) {
        _isAlarmSet.value = boolean
    }
}
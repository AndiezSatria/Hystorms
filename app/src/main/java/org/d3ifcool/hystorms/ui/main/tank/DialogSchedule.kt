package org.d3ifcool.hystorms.ui.main.tank

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.databinding.DialogScheduleBinding
import org.d3ifcool.hystorms.extension.IntExt.combine
import org.d3ifcool.hystorms.model.Schedule
import org.d3ifcool.hystorms.receiver.ScheduleAlarmReceiver
import org.d3ifcool.hystorms.viewmodel.ScheduleViewModel
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class DialogSchedule : BottomSheetDialogFragment() {
    private lateinit var binding: DialogScheduleBinding
    private val viewModel: ScheduleViewModel by viewModels()

    @Inject
    lateinit var alarmReceiver: ScheduleAlarmReceiver
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_schedule, null, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            scheduleViewModel = viewModel
            switchAlarm.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    val schedule = getSchedule()
                    if (schedule != null) setAlarm(schedule, getSelectedDay())
                } else {
                    val schedule = getSchedule()
                    if (schedule != null) cancelAlarm(schedule, getSelectedDay())
                }
            }
        }
        observeSchedule()
        return binding.root
    }

    private fun observeSchedule() {
        viewModel.schedule.observe(viewLifecycleOwner) { schedule ->
            if (schedule != null) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = schedule.id
                val id = calendar.get(Calendar.DATE).combine(
                    calendar.get(Calendar.HOUR_OF_DAY).combine(calendar.get(Calendar.MINUTE))
                ).combine(getSelectedDay())
                val time = Calendar.getInstance()
                time.time = schedule.time
                if (alarmReceiver.isAlarmSet(requireContext(), id)) binding.switchAlarm.isChecked =
                    true
                binding.timePicker.isEnabled = false
                binding.timePicker.hour = time.get(Calendar.HOUR_OF_DAY)
                binding.timePicker.minute = time.get(Calendar.MINUTE)
                var text = ""
                schedule.day.forEach {
                    text += when (it) {
                        0 -> "Minggu, "
                        1 -> "Senin, "
                        2 -> "Selasa, "
                        3 -> "Rabu, "
                        4 -> "Kamis, "
                        5 -> "Jumat, "
                        6 -> "Sabtu, "
                        else -> "Tidak ada"
                    }
                }
                binding.tvDays.text = text
            }
        }
    }

    private fun setAlarm(schedule: Schedule, selectedDay: Int) {
        val time = Calendar.getInstance()
        val startTime = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = schedule.id
        val id = calendar.get(Calendar.DATE).combine(
            calendar.get(Calendar.HOUR_OF_DAY).combine(calendar.get(Calendar.MINUTE))
        ).combine(selectedDay)
        time.time = schedule.time
        startTime.set(Calendar.DAY_OF_WEEK, selectedDay + 1)
        startTime.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY))
        startTime.set(Calendar.MINUTE, time.get(Calendar.MINUTE))
        Action.showLog(startTime.toString())
        alarmReceiver.setRepeatingTimeAlarm(
            requireContext(),
            id,
            startTime,
            "Segera lakukan ${schedule.title} untuk menyuburkan tanamanmu.",
            schedule.title
        )
    }

    private fun cancelAlarm(schedule: Schedule, selectedDay: Int) {
        val time = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = schedule.id
        val id = calendar.get(Calendar.DATE).combine(
            calendar.get(Calendar.HOUR_OF_DAY).combine(calendar.get(Calendar.MINUTE))
        ).combine(selectedDay)
        time.time = schedule.time
        alarmReceiver.cancelAlarm(
            requireContext(),
            id,
            schedule.title
        )
    }

    private fun getSchedule(): Schedule? {
        var schedule: Schedule? = null
        viewModel.schedule.observe(viewLifecycleOwner) { if (it != null) schedule = it }
        return schedule
    }

    private fun getSelectedDay(): Int {
        var day = 0
        viewModel.selectedDay.observe(viewLifecycleOwner) { if (it != null) day = it }
        return day
    }
}
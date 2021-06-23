package org.d3ifcool.hystorms.ui.main.tank

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.databinding.FragmentAddScheduleBinding
import org.d3ifcool.hystorms.model.DayPicker
import org.d3ifcool.hystorms.model.Schedule
import org.d3ifcool.hystorms.viewmodel.AddScheduleViewModel
import java.util.*

@AndroidEntryPoint
class AddScheduleFragment : Fragment(R.layout.fragment_add_schedule) {
    private lateinit var binding: FragmentAddScheduleBinding
    private lateinit var adapter: DayPickerAdapter
    private val viewModel: AddScheduleViewModel by viewModels()

    private val handler = object : DayPickerAdapter.ClickHandler {
        override fun onClick(item: DayPicker, position: Int) {
            adapter.toggleSelection(position)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddScheduleBinding.bind(view)
        adapter = DayPickerAdapter(handler).apply {
            submitList(Constant.DAY_PICKER_DATA)
            notifyDataSetChanged()
        }
        val navHostFragment = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navHostFragment.graph)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            scheduleViewModel = viewModel

            toolbar.setupWithNavController(navHostFragment, appBarConfiguration)

            rvDayPicker.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvDayPicker.adapter = adapter
            timePicker.setIs24HourView(true)

            viewModel.isEdit.observe(viewLifecycleOwner) { isEdit ->
                if (isEdit) {
                    fabDone.setOnClickListener {
                        var scheduleEdited = getSchedule()
                        val scheduleEdit = getScheduleEdit()
                        if (scheduleEdited != null && scheduleEdit != null) {
                            scheduleEdited = scheduleEdited.copy(
                                id = scheduleEdit.id,
                                owner = scheduleEdit.owner,
                                tank = scheduleEdit.tank
                            )
                            viewModel.updateSchedule(scheduleEdited)
                        }
                    }
                } else if (!isEdit) {
                    fabDone.setOnClickListener {
                        val schedule = getSchedule()
                        if (schedule != null) {
                            viewModel.addSchedule(schedule)
                        }
                    }
                }
            }
        }

        observeScheduleEdit()
        observeMessage()
        observeMessageUpdate()
    }

    private fun observeMessage() {
        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Action.showSnackBar(
                    binding.container,
                    message,
                    Snackbar.LENGTH_LONG
                )
                viewModel.doneShowMessage()
            }
        }
    }

    private fun observeMessageUpdate() {
        viewModel.messageUpdate.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Action.showToast(
                    requireActivity(),
                    message
                )
                adapter.clearSelection()
                viewModel.doneShowMessageUpdate()
                findNavController().navigateUp()
            }
        }
    }

    private fun observeScheduleEdit() {
        viewModel.scheduleEdit.observe(viewLifecycleOwner) { schedule ->
            if (schedule != null) {
                schedule.day.forEach { sch ->
                    adapter.toggleSelection(sch)
                }
                val calendar = Calendar.getInstance()
                calendar.time = schedule.time
                binding.timePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
                binding.timePicker.minute = calendar.get(Calendar.MINUTE)
            }
        }
    }

    private fun getTankId(): String {
        var id = ""
        viewModel.tankId.observe(viewLifecycleOwner) {
            if (it != null) id = it
        }
        return id
    }

    private fun getScheduleEdit(): Schedule? {
        var schedule: Schedule? = null
        viewModel.scheduleEdit.observe(viewLifecycleOwner) {
            if (it != null) schedule = it
        }
        return schedule
    }

    private fun getUid(): String {
        var uid = ""
        viewModel.uid.observe(viewLifecycleOwner) {
            if (it != null) uid = it
        }
        return uid
    }

    private fun getSchedule(): Schedule? {
        if (binding.tfName.editText?.text.toString().trim() == "") {
            Action.showSnackBar(binding.container, "Nama harus diisi.", Snackbar.LENGTH_LONG)
            return null
        }
        if (adapter.getSelectionItems().isEmpty()) {
            Action.showSnackBar(binding.container, "Hari harap dipilih.", Snackbar.LENGTH_LONG)
            return null
        }
        val calendar = Calendar.getInstance()
        val time = Calendar.getInstance()
        time.set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
        time.set(Calendar.MINUTE, binding.timePicker.minute)
        return Schedule(
            id = calendar.timeInMillis,
            title = binding.tfName.editText?.text.toString().trim(),
            owner = getUid(),
            tank = getTankId(),
            time = time.time,
            day = adapter.getSelectionItems().map { it.id }
        )
    }
}
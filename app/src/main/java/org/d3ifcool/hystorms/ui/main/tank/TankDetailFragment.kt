package org.d3ifcool.hystorms.ui.main.tank

import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.databinding.FragmentTankDetailBinding
import org.d3ifcool.hystorms.extension.IntExt.combine
import org.d3ifcool.hystorms.model.Schedule
import org.d3ifcool.hystorms.model.Tank
import org.d3ifcool.hystorms.receiver.ScheduleAlarmReceiver
import org.d3ifcool.hystorms.ui.main.home.ScheduleAdapter
import org.d3ifcool.hystorms.viewmodel.DetailTankViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TankDetailFragment : Fragment(R.layout.fragment_tank_detail) {

    private lateinit var binding: FragmentTankDetailBinding
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var dataSensorAdapter: DataSensorAdapter
    private var actionMode: ActionMode? = null
    private val viewModel: DetailTankViewModel by viewModels()
    private var selectedDay: Int = 0

    @Inject
    lateinit var alarmReceiver: ScheduleAlarmReceiver

    private val scheduleHandler = object : ScheduleAdapter.ScheduleClickHandler {
        override fun onClick(item: Schedule, position: Int) {
            if (actionMode != null) {
                scheduleAdapter.toggleSelection(position)
                if (scheduleAdapter.getSelectionItems()
                        .isEmpty()
                ) actionMode?.finish() else actionMode?.invalidate()
                return
            } else {
                findNavController().navigate(
                    TankDetailFragmentDirections.actionTankDetailFragmentToDialogSchedule(
                        item,
                        selectedDay
                    )
                )
            }
        }

        override fun onLongClick(position: Int): Boolean {
            if (actionMode != null) return false
            scheduleAdapter.toggleSelection(position)
            actionMode = requireActivity().startActionMode(actionModeCallback)
            return true
        }
    }

    private val spinnerHandler = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            var tankId = ""
            viewModel.tank.observe(viewLifecycleOwner) {
                if (it != null) tankId = it.id
            }
            selectedDay = position
            scheduleAdapter.clearSelection()
            actionMode?.finish()
            actionMode = null
            viewModel.getSchedule(tankId, position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.menuInflater?.inflate(R.menu.nutrition_action_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.title = "${scheduleAdapter.getSelectionItems().size} data terpilih"
            menu?.findItem(R.id.action_edit)?.isVisible =
                scheduleAdapter.getSelectionItems().size == 1
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.action_edit -> {
                    val schedule = scheduleAdapter.getSelectedItemForEdit()
                    schedule.day.forEach {
                        cancelAlarm(schedule, it)
                    }
                    findNavController().navigate(
                        TankDetailFragmentDirections.actionTankDetailFragmentToAddScheduleFragment(
                            getUid(),
                            getTankId(),
                            schedule
                        )
                    )
                    true
                }
                R.id.action_delete -> {
                    val itemsId: List<Long> = scheduleAdapter.getSelectionItems().map { it.id }
                    Action.showAreYouSureDialog(
                        "Hapus Nutrisi",
                        "Hapus semua data yang dipilih ?",
                        requireContext(),
                        confirmListener = { _, _ ->
                            scheduleAdapter.getSelectionItems().forEach { sch ->
                                sch.day.forEach {
                                    cancelAlarm(sch, it)
                                }
                            }
                            viewModel.deleteSchedule(
                                itemsId, selectedDay
                            )
                            actionMode?.finish()
                        },
                        confirmText = "Hapus",
                        cancelText = "Batal",
                        cancelListener = { dialog, _ ->
                            dialog.dismiss()
                            actionMode?.finish()
                        }
                    ).show()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            scheduleAdapter.clearSelection()
            actionMode?.finish()
            actionMode = null
        }
    }

    override fun onPause() {
        scheduleAdapter.clearSelection()
        actionMode?.finish()
        actionMode = null
        super.onPause()
    }

    private fun getHistoryId(): String {
        var id = ""
        viewModel.tank.observe(viewLifecycleOwner) {
            if (it != null) id = it.id
        }
        return id
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTankDetailBinding.bind(view)
        scheduleAdapter = ScheduleAdapter(scheduleHandler)
        dataSensorAdapter = DataSensorAdapter()

        val navHostFragment = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navHostFragment.graph)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            tankViewModel = viewModel

            toolbar.setupWithNavController(navHostFragment, appBarConfiguration)

            rvResult.layoutManager =
                GridLayoutManager(requireActivity(), 3, GridLayoutManager.HORIZONTAL, false)
            rvResult.adapter = dataSensorAdapter
            rvSchedule.adapter = scheduleAdapter

            val calendar = Calendar.getInstance()
            val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
            tvDate.text = formatter.format(calendar.time)
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.item_days,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
            spinner.setSelection(calendar.get(Calendar.DAY_OF_WEEK) - 1)
            spinner.onItemSelectedListener = spinnerHandler

            btnHistory.setOnClickListener {
                findNavController().navigate(
                    TankDetailFragmentDirections.actionTankDetailFragmentToHistoryFragment(
                        getHistoryId()
                    )
                )
            }

            layoutData.btnEditTank.setOnClickListener {
                findNavController().navigate(
                    TankDetailFragmentDirections.actionTankDetailFragmentToEditTankFragment(
                        getTankEdit()
                    )
                )
            }

            btnAddSchedule.setOnClickListener {
                findNavController().navigate(
                    TankDetailFragmentDirections.actionTankDetailFragmentToAddScheduleFragment(
                        getUid(),
                        getTankId(),
                        null
                    )
                )
            }
        }
        observeSnackBarMessage()
        observePlant()
        observeTank()
        observeSchedule()
        onMenuItemClicked()
        observeTankId()
    }

    private fun observeSnackBarMessage() {
        viewModel.snackBarMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Action.showSnackBar(binding.coordinator, message, Snackbar.LENGTH_LONG)
                viewModel.doneShowingMessage()
            }
        }
    }

    private fun observeTankId() {
        val calendar = Calendar.getInstance()
        viewModel.tankId.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.getTank(it)
                selectedDay = calendar.get(Calendar.DAY_OF_WEEK) - 1
                viewModel.getSchedule(it, calendar.get(Calendar.DAY_OF_WEEK) - 1)
            }
        }
    }

    private fun observePlant() {
        viewModel.plant.observe(viewLifecycleOwner) { plant ->
            if (plant != null) binding.layoutData.plant = plant
        }
    }

    private fun observeTank() {
        viewModel.tank.observe(viewLifecycleOwner) { tank ->
            if (tank != null) {
                Action.showLog(tank.toString())
                viewModel.getPlant(tank.plant)
                binding.layoutData.tank = tank
                dataSensorAdapter.submitList(tank.sensorData)
                dataSensorAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun observeSchedule() {
        viewModel.schedule.observe(viewLifecycleOwner) { listSchedule ->
            if (listSchedule != null) {
                Action.showLog(listSchedule.toString())
                scheduleAdapter.submitList(listSchedule)
                scheduleAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun onMenuItemClicked() {
        binding.toolbar.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.share -> {
                    var message = ""
                    viewModel.tank.observe(viewLifecycleOwner) {
                        if (it != null) message += it.toString()
                    }
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, message)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                    true
                }
                else -> false
            }
        }
    }

    private fun getUid(): String {
        var uid = ""
        viewModel.uid.observe(viewLifecycleOwner) {
            if (it != null) uid = it
        }
        return uid
    }

    private fun getTankId(): String {
        var id = ""
        viewModel.tank.observe(viewLifecycleOwner) {
            if (it != null) id = it.id
        }
        return id
    }

    private fun getTankEdit(): Tank? {
        var tank: Tank? = null
        viewModel.tank.observe(viewLifecycleOwner) {
            if (it != null) tank = it
        }
        return tank
    }
}
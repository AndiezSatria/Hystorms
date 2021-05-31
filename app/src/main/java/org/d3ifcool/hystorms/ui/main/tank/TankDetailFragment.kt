package org.d3ifcool.hystorms.ui.main.tank

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.databinding.FragmentTankDetailBinding
import org.d3ifcool.hystorms.model.Schedule
import org.d3ifcool.hystorms.ui.main.home.ScheduleAdapter
import org.d3ifcool.hystorms.util.ItemClickHandler
import org.d3ifcool.hystorms.viewmodel.DetailTankViewModel
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class TankDetailFragment : Fragment(R.layout.fragment_tank_detail) {

    private lateinit var binding: FragmentTankDetailBinding
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var dataSensorAdapter: DataSensorAdapter
    private val viewModel: DetailTankViewModel by viewModels()

    private val scheduleHandler = object : ItemClickHandler<Schedule> {
        override fun onClick(item: Schedule) {}

        override fun onItemDelete(item: Schedule) {}
    }

    private val spinnerHandler = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            var tankId = ""
            viewModel.tank.observe(viewLifecycleOwner) {
                if (it != null) tankId = it.id
            }
            viewModel.getSchedule(tankId, position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTankDetailBinding.bind(view)
        scheduleAdapter = ScheduleAdapter(scheduleHandler)
        dataSensorAdapter = DataSensorAdapter()

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            tankViewModel = viewModel

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
            spinner.setSelection(calendar.get(Calendar.DAY_OF_WEEK))
            spinner.onItemSelectedListener = spinnerHandler
        }
        observeSnackBarMessage()
        observePlant()
        observeTank()
        observeSchedule()
    }

    private fun observeSnackBarMessage() {
        viewModel.snackBarMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Action.showSnackBar(binding.coordinator, message, Snackbar.LENGTH_LONG)
                viewModel.doneShowingMessage()
            }
        }
    }

    private fun observePlant() {
        viewModel.plant.observe(viewLifecycleOwner) { plant ->
            if (plant != null) binding.layoutData.plant = plant
        }
    }

    private fun observeTank() {
        val calendar = Calendar.getInstance()
        viewModel.tank.observe(viewLifecycleOwner) { tank ->
            if (tank != null) {
                viewModel.getPlant(tank.plant)
                viewModel.getSchedule(tank.id, calendar.get(Calendar.DAY_OF_WEEK))
                binding.layoutData.tank = tank
                dataSensorAdapter.submitList(tank.sensorData)
                dataSensorAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun observeSchedule() {
        viewModel.schedule.observe(viewLifecycleOwner) { listSchedule ->
            if (listSchedule != null) {
                scheduleAdapter.submitList(listSchedule)
                scheduleAdapter.notifyDataSetChanged()
            }
        }
    }
}
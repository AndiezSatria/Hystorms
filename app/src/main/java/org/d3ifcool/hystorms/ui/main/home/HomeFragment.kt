package org.d3ifcool.hystorms.ui.main.home

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.databinding.FragmentHomeBinding
import org.d3ifcool.hystorms.model.Schedule
import org.d3ifcool.hystorms.model.Tank
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.ui.main.MainFragmentDirections
import org.d3ifcool.hystorms.util.ItemClickHandler
import org.d3ifcool.hystorms.util.ViewState
import org.d3ifcool.hystorms.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var homeTankAdapter: HomeTankAdapter
    private lateinit var scheduleAdapter: ScheduleAdapter

    private val spinnerHandler = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            var userId = ""
            viewModel.uid.observe(viewLifecycleOwner) {
                if (it != null) userId = it
            }
            viewModel.getSchedule(userId, position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}

    }

    private val tankHandler = object : ItemClickHandler<Tank> {
        override fun onClick(item: Tank) {
            Navigation.findNavController(requireActivity(), R.id.nav_main).navigate(
                MainFragmentDirections.actionMainFragmentToTankDetailFragment(item)
            )
        }

        override fun onItemDelete(item: Tank) {}

    }
    private val scheduleHandler = object : ItemClickHandler<Schedule> {
        override fun onClick(item: Schedule) {}
        override fun onItemDelete(item: Schedule) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments = requireActivity().intent.extras
        binding = FragmentHomeBinding.bind(view)

        homeTankAdapter = HomeTankAdapter(tankHandler)
        scheduleAdapter = ScheduleAdapter(scheduleHandler)
        val horizontalLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            homeViewModel = viewModel
            val calendar = Calendar.getInstance()
            val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))

            rvTank.layoutManager = horizontalLayoutManager
            rvTank.adapter = homeTankAdapter

            tvDate.text = formatter.format(calendar.time)
            rvSchedule.adapter = scheduleAdapter

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

            viewModel.weatherViewState.observe(viewLifecycleOwner) {
                weatherLayout.viewState = it
            }

            btnWeatherRetry.setOnClickListener {
                observeDevice()
            }
        }
        observeWeatherData()
        observeUid()
        observeDevice()
        observeUser()
        observeUid()
        observeScheduleData()
        observeTankData()
    }

    private fun observeUser() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                if (user.favoriteDevice != null) viewModel.getDevice(user.favoriteDevice!!)
                else getWeatherDataFromGps()
            }
        }
    }

    private fun observeUid() {
        val calendar = Calendar.getInstance()
        viewModel.uid.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.getTanks(it)
                viewModel.getUserState(it)
                viewModel.getSchedule(it, calendar.get(Calendar.DAY_OF_WEEK))
            }
        }
    }

    private fun observeDevice() {
        viewModel.device.observe(viewLifecycleOwner) { device ->
            if (device != null) {
                viewModel.getWeather(
                    device.latitude,
                    device.longitude,
                    "id"
                )
            } else getWeatherDataFromGps()
        }
    }

    private fun observeWeatherData() {
        viewModel.weather.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Canceled -> {
                    viewModel.setWeatherViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.container,
                        dataState.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                }
                is DataState.Error -> {
                    viewModel.setWeatherViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.container,
                        dataState.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                }
                is DataState.Loading -> viewModel.setWeatherViewState(ViewState.LOADING)
                is DataState.Success -> {
                    viewModel.setWeatherViewState(ViewState.SUCCESS)
                    binding.weatherLayout.weather = dataState.data
                }
            }
        }
    }

    private fun observeTankData() {
        viewModel.tanksDataState.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Canceled -> {
                    viewModel.setTankViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.container,
                        dataState.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                }
                is DataState.Error -> {
                    viewModel.setTankViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.container,
                        dataState.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                }
                is DataState.Loading -> viewModel.setTankViewState(ViewState.LOADING)
                is DataState.Success -> {
                    homeTankAdapter.submitList(dataState.data)
                    homeTankAdapter.notifyDataSetChanged()
                    viewModel.setTankViewState(ViewState.SUCCESS)
                }
            }
        }
    }

    private fun observeScheduleData() {
        viewModel.schedulesDataState.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Canceled -> {
                    viewModel.setScheduleViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.container,
                        dataState.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                }
                is DataState.Error -> {
                    viewModel.setScheduleViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.container,
                        dataState.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                }
                is DataState.Loading -> viewModel.setScheduleViewState(ViewState.LOADING)
                is DataState.Success -> {
                    scheduleAdapter.submitList(dataState.data)
                    scheduleAdapter.notifyDataSetChanged()
                    viewModel.setScheduleViewState(ViewState.SUCCESS)
                }
            }
        }
    }

    private fun getWeatherDataFromGps() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (Action.checkPermission(requireActivity())) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null)
                    viewModel.getWeather(
                        location.latitude,
                        location.longitude,
                        "id"
                    )
                else {
                    Action.showSnackBar(
                        binding.container,
                        "Lokasi tidak diketahui",
                        Snackbar.LENGTH_SHORT
                    )
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }
}
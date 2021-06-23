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
import org.d3ifcool.hystorms.util.EspressoIdlingResource
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
    private var selectedDay: Int = 0

    private val spinnerHandler = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            var userId = ""
            viewModel.uid.observe(viewLifecycleOwner) {
                if (it != null) userId = it
            }
            selectedDay = position
            viewModel.getSchedule(userId, position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}

    }

    private val tankHandler = object : ItemClickHandler<Tank> {
        override fun onClick(item: Tank) {
            Navigation.findNavController(requireActivity(), R.id.nav_main).navigate(
                MainFragmentDirections.actionMainFragmentToTankDetailFragment(getUid(), item.id)
            )
        }

        override fun onItemDelete(item: Tank) {}

    }
    private val scheduleHandler = object : ScheduleAdapter.ScheduleClickHandler {
        override fun onClick(item: Schedule, position: Int) {
            Navigation.findNavController(requireActivity(), R.id.nav_main)
                .navigate(
                    MainFragmentDirections.actionMainFragmentToDialogSchedule(
                        item,
                        selectedDay
                    )
                )
        }

        override fun onLongClick(position: Int): Boolean = true
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
            spinner.setSelection(calendar.get(Calendar.DAY_OF_WEEK) - 1)
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

    private fun getUid(): String {
        var uid = ""
        viewModel.uid.observe(viewLifecycleOwner) {
            if (it != null) uid = it
        }
        return uid
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
                viewModel.getSchedule(it, calendar.get(Calendar.DAY_OF_WEEK) - 1)
                selectedDay = calendar.get(Calendar.DAY_OF_WEEK) - 1
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
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    viewModel.setWeatherViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.container,
                        dataState.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                }
                is DataState.Error -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    viewModel.setWeatherViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.container,
                        dataState.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                }
                is DataState.Loading -> {
                    EspressoIdlingResource.increment()
                    viewModel.setWeatherViewState(ViewState.LOADING)
                }
                is DataState.Success -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    viewModel.setWeatherViewState(ViewState.SUCCESS)
                    binding.weatherLayout.weather = dataState.data
                }
                is DataState.ErrorThrowable -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    viewModel.setWeatherViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.container,
                        dataState.throwable.message,
                        Snackbar.LENGTH_LONG
                    )
                }
            }
        }
    }

    private fun observeTankData() {
        viewModel.tanksDataState.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Canceled -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    viewModel.setTankViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.container,
                        dataState.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                }
                is DataState.Error -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    viewModel.setTankViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.container,
                        dataState.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                }
                is DataState.Loading -> {
                    EspressoIdlingResource.increment()
                    viewModel.setTankViewState(ViewState.LOADING)
                }
                is DataState.Success -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    homeTankAdapter.submitList(dataState.data)
                    viewModel.setIsTankEmpty(dataState.data.isEmpty())
                    homeTankAdapter.notifyDataSetChanged()
                    viewModel.setTankViewState(ViewState.SUCCESS)
                }
                is DataState.ErrorThrowable -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    dataState.throwable.message?.let { Action.showLog(it) }
                    viewModel.setTankViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.container,
                        dataState.throwable.message,
                        Snackbar.LENGTH_LONG
                    )
                }
            }
        }
    }

    private fun observeScheduleData() {
        viewModel.schedulesDataState.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Canceled -> {
                    dataState.exception.message?.let { Action.showLog(it) }
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    viewModel.setScheduleViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.container,
                        dataState.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                }
                is DataState.Error -> {
                    dataState.exception.message?.let { Action.showLog(it) }
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    viewModel.setScheduleViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.container,
                        dataState.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                }
                is DataState.Loading -> {
                    EspressoIdlingResource.increment()
                    viewModel.setScheduleViewState(ViewState.LOADING)
                }
                is DataState.Success -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    scheduleAdapter.submitList(dataState.data)
                    viewModel.setIsTankEmpty(dataState.data.isEmpty())
                    scheduleAdapter.notifyDataSetChanged()
                    viewModel.setScheduleViewState(ViewState.SUCCESS)
                }
                is DataState.ErrorThrowable -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    dataState.throwable.message?.let { Action.showLog(it) }
                    viewModel.setTankViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.container,
                        dataState.throwable.message,
                        Snackbar.LENGTH_LONG
                    )
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
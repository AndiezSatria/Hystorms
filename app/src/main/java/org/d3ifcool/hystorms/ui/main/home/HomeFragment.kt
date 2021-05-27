package org.d3ifcool.hystorms.ui.main.home

import android.Manifest
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.databinding.FragmentHomeBinding
import org.d3ifcool.hystorms.model.Tank
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.ItemClickHandler
import org.d3ifcool.hystorms.util.ViewState
import org.d3ifcool.hystorms.viewmodel.HomeViewModel

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var homeTankAdapter: HomeTankAdapter

    private val handler = object : ItemClickHandler<Tank> {
        override fun onClick(item: Tank) {

        }

        override fun onItemDelete(item: Tank) {}

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments = requireActivity().intent.extras
        binding = FragmentHomeBinding.bind(view)

        homeTankAdapter = HomeTankAdapter(handler)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            homeViewModel = viewModel

            rvTank.adapter = homeTankAdapter
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
    }

    private fun observeUser() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                viewModel.getTanks(user.uid)
                if (user.favoriteDevice != null) viewModel.getDevice(user.favoriteDevice!!)
                else getWeatherDataFromGps()
            }
        }
    }

    private fun observeUid() {
        viewModel.uid.observe(viewLifecycleOwner) {
            if (it != null) viewModel.getUserState(it)
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
                    homeTankAdapter.submitList(dataState.data)
                    homeTankAdapter.notifyDataSetChanged()
                    viewModel.setWeatherViewState(ViewState.SUCCESS)
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
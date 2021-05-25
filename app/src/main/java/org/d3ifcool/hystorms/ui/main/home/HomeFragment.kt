package org.d3ifcool.hystorms.ui.main.home

import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.databinding.FragmentHomeBinding
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.ViewState
import org.d3ifcool.hystorms.viewmodel.HomeViewModel

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments = requireActivity().intent.extras
        binding = FragmentHomeBinding.bind(view)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (Action.checkPermission(requireContext())) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null)
                    viewModel.getWeather(
                        location.latitude,
                        location.longitude,
                        "id"
                    )
                else {
                    Action.showLog("Gagal")
                }
            }
        }
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            homeViewModel = viewModel

            viewModel.weatherViewState.observe(viewLifecycleOwner) {
                weatherLayout.viewState = it
            }
        }
        viewModel.user.observe(viewLifecycleOwner) {
            if (it != null) Action.showLog(it.toString())
        }
        observeWeatherData()
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
}
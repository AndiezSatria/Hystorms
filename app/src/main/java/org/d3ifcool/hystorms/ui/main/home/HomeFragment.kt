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
import org.d3ifcool.hystorms.viewmodel.HomeViewModel

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (Action.checkPermission(requireContext())) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null)
                    viewModel.getWeather(
                        location.latitude.toLong(),
                        location.longitude.toLong(),
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

        observeWeatherData()
    }

    private fun observeWeatherData() {
        viewModel.weather.observe(viewLifecycleOwner) { dataOrException ->
            if (dataOrException.data != null) {
                val weather = dataOrException.data!!
                binding.weatherLayout.weather = weather
            }
            if (dataOrException.exception != null) {
                dataOrException.exception?.message?.let {
                    Action.showSnackBar(binding.container, it, Snackbar.LENGTH_LONG)
                }
            }
        }
    }
}
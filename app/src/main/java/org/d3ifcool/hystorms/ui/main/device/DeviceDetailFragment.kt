package org.d3ifcool.hystorms.ui.main.device

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.schibstedspain.leku.LATITUDE
import com.schibstedspain.leku.LOCATION_ADDRESS
import com.schibstedspain.leku.LONGITUDE
import com.schibstedspain.leku.LocationPickerActivity
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.databinding.ChipSensorConditionBinding
import org.d3ifcool.hystorms.databinding.FragmentDeviceDetailBinding
import org.d3ifcool.hystorms.model.Device
import org.d3ifcool.hystorms.model.MyAddress
import org.d3ifcool.hystorms.model.SensorPhysic
import org.d3ifcool.hystorms.model.Tank
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.ItemClickHandler
import org.d3ifcool.hystorms.util.ViewState
import org.d3ifcool.hystorms.viewmodel.DetailDeviceViewModel
import java.util.*

@AndroidEntryPoint
class DeviceDetailFragment : Fragment(R.layout.fragment_device_detail) {

    private lateinit var binding: FragmentDeviceDetailBinding
    private lateinit var tankAdapter: TankAdapter
    private val viewModel: DetailDeviceViewModel by viewModels()

    private val handler = object : ItemClickHandler<Tank> {
        override fun onClick(item: Tank) {
            findNavController().navigate(
                DeviceDetailFragmentDirections.actionDeviceDetailFragmentToTankDetailFragment(
                    getUid(), item.id
                )
            )
        }

        override fun onItemDelete(item: Tank) {}
    }

    private val chipListener = object : ChipClickListener<SensorPhysic, Date> {
        override fun onClick(item1: SensorPhysic, item2: Date) {
            Action.showLog(item1.toString())
            Action.showLog(item2.toString())
            SensorConditionDialog.newInstance(item1, item2)
                .show(requireActivity().supportFragmentManager, "SensorConditionDialog")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDeviceDetailBinding.bind(view)
        tankAdapter = TankAdapter(handler)

        val navHostFragment = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navHostFragment.graph)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            detailViewModel = viewModel
            rvTank.adapter = tankAdapter
            toolbar.setupWithNavController(navHostFragment, appBarConfiguration)
            viewModel.weatherViewState.observe(viewLifecycleOwner) {
                weatherLayout.viewState = it
            }

            layoutData.btnEditLocation.setOnClickListener {
                var lat = 0.0
                var lon = 0.0
                viewModel.device.observe(viewLifecycleOwner) {
                    if (it != null) {
                        lat = it.latitude
                        lon = it.longitude
                    }
                }
                val locationPickerIntent = LocationPickerActivity.Builder()
                    .withLocation(lat, lon)
                    .withSearchZone("id_ID")
                    .withDefaultLocaleSearchZone()
                    .withGoogleTimeZoneEnabled()
                    .withUnnamedRoadHidden()
                    .build(requireContext())
                startActivityForResult(locationPickerIntent, 1)
            }
        }
        observerIsFavorite()
        observeDevice()
        observeCondition()
        observeTank()
        onMenuItemClicked()
        observeSnackbarMessage()
        observeMyAddress()
        observeWeather()
    }

    private fun observeWeather() {
        viewModel.weather.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.weatherLayout.weather = it
            }
        }
    }

    private fun getUid(): String {
        var uid = ""
        viewModel.user.observe(viewLifecycleOwner) {
            if (it != null) uid = it.uid
        }
        return uid
    }

    private fun observeSnackbarMessage() {
        viewModel.snackbarMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Action.showSnackBar(binding.coordinator, message, Snackbar.LENGTH_SHORT)
                viewModel.doneShowingSnackbar()
            }
        }
    }

    private fun observeDevice() {
        viewModel.device.observe(viewLifecycleOwner) { device ->
            if (device != null) {
                binding.layoutData.device = device
                viewModel.getCondition(device.conditions)
                viewModel.getTank(device.id)
                viewModel.getWeather(device.latitude, device.longitude, "id")
                binding.btnWeatherRetry.setOnClickListener {
                    viewModel.getWeather(device.latitude, device.longitude, "id")
                }
            }
        }
    }

    private fun observeCondition() {
        viewModel.conditionState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Canceled -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.message,
                        Snackbar.LENGTH_SHORT
                    )
                    binding.layoutData.chipGroup.visibility = View.GONE
                    viewModel.setDetailViewState(ViewState.SUCCESS)
                }
                is DataState.Error -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.message,
                        Snackbar.LENGTH_SHORT
                    )
                    binding.layoutData.chipGroup.visibility = View.GONE
                    viewModel.setDetailViewState(ViewState.SUCCESS)
                }
                is DataState.Loading -> {
                    EspressoIdlingResource.increment()
                    viewModel.setDetailViewState(ViewState.LOADING)
                }
                is DataState.Success -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    val condition = state.data
                    val listData = condition.data
                    binding.layoutData.chipGroup.removeAllViewsInLayout()
                    listData.forEach { sensor ->
                        val inflater = LayoutInflater.from(requireContext())
                        val chipBinding: ChipSensorConditionBinding = DataBindingUtil.inflate(
                            inflater,
                            R.layout.chip_sensor_condition,
                            null,
                            false
                        )
                        chipBinding.sensor = sensor
                        chipBinding.root.setOnClickListener {
                            chipListener.onClick(
                                sensor,
                                condition.timeStamp
                            )
                        }
                        binding.layoutData.chipGroup.addView(chipBinding.root)
                    }
                    viewModel.setCondition(listData)
                    viewModel.setDetailViewState(ViewState.SUCCESS)
                }
                is DataState.ErrorThrowable -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    Action.showSnackBar(
                        binding.coordinator,
                        state.throwable.message,
                        Snackbar.LENGTH_SHORT
                    )
                    binding.layoutData.chipGroup.visibility = View.GONE
                    viewModel.setDetailViewState(ViewState.SUCCESS)
                }
            }
        }
    }

    private fun observerIsFavorite() {
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            if (isFavorite != null) {
                if (isFavorite) {
                    val menuFavorite = binding.toolbar.menu.findItem(R.id.favorite)
                    menuFavorite.isVisible = false
                    val menuDeleteFavorite = binding.toolbar.menu.findItem(R.id.delete_favorite)
                    menuDeleteFavorite.isVisible = true
                } else {
                    val menuFavorite = binding.toolbar.menu.findItem(R.id.favorite)
                    menuFavorite.isVisible = true
                    val menuDeleteFavorite = binding.toolbar.menu.findItem(R.id.delete_favorite)
                    menuDeleteFavorite.isVisible = false
                }
            }
        }
    }

    private fun observeTank() {
        viewModel.tanksState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Canceled -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.message,
                        Snackbar.LENGTH_SHORT
                    )
                    viewModel.setTankViewState(ViewState.ERROR)
                }
                is DataState.Error -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.message,
                        Snackbar.LENGTH_SHORT
                    )
                    viewModel.setTankViewState(ViewState.ERROR)
                }
                is DataState.Loading -> {
                    EspressoIdlingResource.increment()
                    viewModel.setTankViewState(ViewState.LOADING)
                }
                is DataState.Success -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    val data = state.data
                    tankAdapter.submitList(data)
                    tankAdapter.notifyDataSetChanged()
                    viewModel.setTankViewState(ViewState.SUCCESS)
                }
                is DataState.ErrorThrowable -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    Action.showSnackBar(
                        binding.coordinator,
                        state.throwable.message,
                        Snackbar.LENGTH_SHORT
                    )
                    viewModel.setTankViewState(ViewState.ERROR)
                }
            }
        }
    }

    private fun onMenuItemClicked() {
        binding.toolbar.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.favorite -> {
                    var userId = ""
                    var deviceId = ""
                    viewModel.user.observe(viewLifecycleOwner) {
                        if (it != null) userId = it.uid
                    }
                    viewModel.device.observe(viewLifecycleOwner) {
                        if (it != null) deviceId = it.id
                    }
                    viewModel.setFavorite(deviceId, userId)
                    true
                }
                R.id.delete_favorite -> {
                    var userId = ""
                    viewModel.user.observe(viewLifecycleOwner) {
                        if (it != null) userId = it.uid
                    }
                    viewModel.setFavorite(null, userId)
                    true
                }
                R.id.share -> {
                    var message = ""
                    viewModel.device.observe(viewLifecycleOwner) {
                        if (it != null) message += it.toString()
                    }
                    viewModel.condition.observe(viewLifecycleOwner) {
                        it?.forEach { data ->
                            message += "\n${data}"
                        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                if (requestCode == 1 && data != null) {
                    val myAddress = MyAddress(
                        1,
                        data.getDoubleExtra(LATITUDE, 0.0),
                        data.getDoubleExtra(
                            LONGITUDE, 0.0
                        ),
                        if (data.getStringExtra(LOCATION_ADDRESS) != "") data.getStringExtra(
                            LOCATION_ADDRESS
                        ) ?: "Lokasi tidak diketahui"
                        else "Lokasi tidak diketahui"
                    )
                    viewModel.setMyAddress(myAddress)
                }
            }
            else -> {
                Action.showToast(requireContext(), "Tugas dibatalkan")
            }
        }
    }

    private fun observeMyAddress() {
        viewModel.myAddress.observe(viewLifecycleOwner) {
            if (it != null) {
                val device = getDevice()
                if (device != null) {
                    viewModel.updateLocation(it, device)
                    viewModel.setMyAddress(null)
                }
            }
        }
    }

    private fun getDevice(): Device? {
        var device: Device? = null
        viewModel.device.observe(viewLifecycleOwner) {
            if (it != null) device = it
        }
        return device
    }

    interface ChipClickListener<T, R> {
        fun onClick(item1: T, item2: R)
    }
}
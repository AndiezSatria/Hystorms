package org.d3ifcool.hystorms.ui.main.device

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.databinding.FragmentDeviceDetailBinding
import org.d3ifcool.hystorms.model.Tank
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.ItemClickHandler
import org.d3ifcool.hystorms.util.ViewState
import org.d3ifcool.hystorms.viewmodel.DetailDeviceViewModel

@AndroidEntryPoint
class DeviceDetailFragment : Fragment(R.layout.fragment_device_detail) {

    private lateinit var binding: FragmentDeviceDetailBinding
    private lateinit var tankAdapter: TankAdapter
    private val viewModel: DetailDeviceViewModel by viewModels()

    private val handler = object : ItemClickHandler<Tank> {
        override fun onClick(item: Tank) {
            findNavController().navigate(
                DeviceDetailFragmentDirections.actionDeviceDetailFragmentToTankDetailFragment(item)
            )
        }

        override fun onItemDelete(item: Tank) {}
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
        }
        observerIsFavorite()
        observeDevice()
        observeCondition()
        observeTank()
        onMenuItemClicked()
        observeSnackbarMessage()
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
            }
        }
    }

    private fun observeCondition() {
        viewModel.conditionState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Canceled -> {
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.message,
                        Snackbar.LENGTH_SHORT
                    )
                    binding.layoutData.chipGroup.visibility = View.GONE
                    viewModel.setDetailViewState(ViewState.SUCCESS)
                }
                is DataState.Error -> {
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.message,
                        Snackbar.LENGTH_SHORT
                    )
                    binding.layoutData.chipGroup.visibility = View.GONE
                    viewModel.setDetailViewState(ViewState.SUCCESS)
                }
                is DataState.Loading -> viewModel.setDetailViewState(ViewState.LOADING)
                is DataState.Success -> {
                    val condition = state.data
                    val listData = condition.data
                    Action.showLog(condition.data.toString())
                    binding.layoutData.temp = listData.find {
                        it.id == 3
                    }
                    binding.layoutData.humidity = listData.find {
                        it.id == 2
                    }
                    binding.layoutData.luminous = listData.find {
                        it.id == 1
                    }
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
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.message,
                        Snackbar.LENGTH_SHORT
                    )
                    viewModel.setTankViewState(ViewState.ERROR)
                }
                is DataState.Error -> {
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.message,
                        Snackbar.LENGTH_SHORT
                    )
                    viewModel.setTankViewState(ViewState.ERROR)
                }
                is DataState.Loading -> viewModel.setTankViewState(ViewState.LOADING)
                is DataState.Success -> {
                    val data = state.data
                    Action.showLog(data.toString())
                    tankAdapter.submitList(data)
                    tankAdapter.notifyDataSetChanged()
                    viewModel.setTankViewState(ViewState.SUCCESS)
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
                else -> false
            }
        }
    }
}
package org.d3ifcool.hystorms.ui.main.device

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.databinding.FragmentDevicesBinding
import org.d3ifcool.hystorms.model.Device
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.ui.main.MainFragmentDirections
import org.d3ifcool.hystorms.util.ItemClickHandler
import org.d3ifcool.hystorms.util.ViewState
import org.d3ifcool.hystorms.viewmodel.DevicesViewModel

@AndroidEntryPoint
class DevicesFragment : Fragment(R.layout.fragment_devices) {
    private lateinit var binding: FragmentDevicesBinding

    private val viewModel: DevicesViewModel by viewModels()
    private lateinit var adapter: DevicesAdapter

    private val handler = object : ItemClickHandler<Device> {
        override fun onClick(item: Device) {
            Navigation.findNavController(requireActivity(), R.id.nav_main).navigate(
                MainFragmentDirections.actionMainFragmentToDeviceDetailFragment(
                    item
                )
            )
        }

        override fun onItemDelete(item: Device) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDevicesBinding.bind(view)
        adapter = DevicesAdapter(handler)
        arguments = requireActivity().intent.extras

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            devicesViewModel = viewModel

            recyclerView.adapter = adapter
            btnRefresh.setOnClickListener {
                viewModel.getDevices()
            }
        }
        observeData()
    }

    private fun observeData() {
        viewModel.devices.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Canceled -> {
                    Action.showSnackBar(
                        binding.container,
                        state.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                    viewModel.setViewState(ViewState.ERROR)
                }
                is DataState.Error -> {
                    Action.showSnackBar(
                        binding.container,
                        state.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                    viewModel.setViewState(ViewState.ERROR)
                }
                is DataState.Loading -> {
                    viewModel.setViewState(ViewState.LOADING)
                }
                is DataState.Success -> {
                    adapter.submitList(state.data)
                    viewModel.setIsEmpty(state.data.isEmpty())
                    viewModel.setViewState(ViewState.SUCCESS)
                }
            }
        }
    }
}
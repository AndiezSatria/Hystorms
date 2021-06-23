package org.d3ifcool.hystorms.ui.main.device

import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.databinding.FragmentDevicesBinding
import org.d3ifcool.hystorms.model.AddDevice
import org.d3ifcool.hystorms.model.Device
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.ui.main.MainFragmentDirections
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.ViewState
import org.d3ifcool.hystorms.viewmodel.DevicesViewModel

@AndroidEntryPoint
class DevicesFragment : Fragment(R.layout.fragment_devices) {
    private lateinit var binding: FragmentDevicesBinding
    private var actionMode: ActionMode? = null
    private val viewModel: DevicesViewModel by viewModels()
    private lateinit var adapter: DevicesAdapter

    private val handler = object : DevicesAdapter.DeviceHandler {
        override fun onClick(position: Int, item: Device) {
            if (actionMode != null) {
                adapter.toggleSelection(position)
                if (adapter.getSelectionItems()
                        .isEmpty()
                ) actionMode?.finish() else actionMode?.invalidate()
                return
            } else {
                var user: User? = null
                viewModel.user.observe(viewLifecycleOwner) {
                    if (it != null) user = it
                }
                Navigation.findNavController(requireActivity(), R.id.nav_main).navigate(
                    MainFragmentDirections.actionMainFragmentToDeviceDetailFragment(item, user)
                )
            }
        }

        override fun onLongClick(position: Int): Boolean {
            if (actionMode != null) return false
            adapter.toggleSelection(position)
            actionMode = requireActivity().startActionMode(actionModeCallback)
            return true
        }
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.menuInflater?.inflate(R.menu.device_action_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.title = "${adapter.getSelectionItems().size} data terpilih"
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.action_delete -> {
                    val itemsId: List<String> = adapter.getSelectionItems().map { it.id }
                    Action.showAreYouSureDialog(
                        "Hapus Alat",
                        "Hapus semua alat yang dipilih ?",
                        requireContext(),
                        confirmListener = { _, _ ->
                            viewModel.deleteOwnership(itemsId)
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
            adapter.clearSelection()
            actionMode?.finish()
            actionMode = null
        }
    }

    private val dialogListener = object : AddDeviceDialog.DialogAddDeviceListener {
        override fun processDialog(device: AddDevice) {
            Action.showLog(device.toString())
            // update device yang dimaksud
            // owner = akun, isAuthorized = false
            // Verifikasi melalui IoT dengan menggunakan email yang disimpan di script IoT
            viewModel.getDevice(device)
        }
    }

    override fun onPause() {
        adapter.clearSelection()
        actionMode?.finish()
        actionMode = null
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDevicesBinding.bind(view)
        adapter = DevicesAdapter(handler)
        arguments = requireActivity().intent.extras

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            devicesViewModel = viewModel

            rvDevice.adapter = adapter
            btnRefresh.setOnClickListener {
                observeUser()
            }

            floatingActionButton.setOnClickListener {
                AddDeviceDialog.newInstance(dialogListener).show(
                    requireActivity().supportFragmentManager,
                    "AddDeviceDialog"
                )
            }
        }
        observeData()
        observeUser()
        observeUid()
        observeDeviceUpdate()
        observeMessage()
        observeMessageUpdate()
    }

    private fun observeUser() {
        viewModel.user.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.getDevices(it)
            }
        }
    }

    private fun observeDeviceUpdate() {
        viewModel.device.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.owner != null) {
                    Action.showSnackBar(
                        binding.container,
                        "Gagal menambahkan alat",
                        Snackbar.LENGTH_LONG
                    )
                } else {
                    val uid = getUid()
                    if (uid != null) {
                        viewModel.updateDevice(uid, it.id)
                        viewModel.doneUpdateDevice()
                    }
                }
            }
        }
    }

    private fun observeMessage() {
        viewModel.message.observe(viewLifecycleOwner) {
            if (it != null) {
                Action.showSnackBar(binding.container, it, Snackbar.LENGTH_LONG)
                Action.showLog(it)
                viewModel.doneShowMessage()
            }
        }
    }

    private fun observeMessageUpdate() {
        viewModel.messageUpdate.observe(viewLifecycleOwner) {
            if (it != null) {
                Action.showSnackBar(binding.container, it, Snackbar.LENGTH_LONG)
                viewModel.doneShowMessageUpdate()
            }
        }
    }

    private fun observeUid() {
        viewModel.uid.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.getUserState(it)
            }
        }
    }

    private fun observeData() {
        viewModel.devices.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Canceled -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    Action.showSnackBar(
                        binding.container,
                        state.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                    viewModel.setViewState(ViewState.ERROR)
                }
                is DataState.Error -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    Action.showSnackBar(
                        binding.container,
                        state.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                    viewModel.setViewState(ViewState.ERROR)
                }
                is DataState.Loading -> {
                    EspressoIdlingResource.increment()
                    viewModel.setViewState(ViewState.LOADING)
                }
                is DataState.Success -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    adapter.submitList(state.data)
                    adapter.notifyDataSetChanged()
                    viewModel.setIsEmpty(state.data.isEmpty())
                    viewModel.setViewState(ViewState.SUCCESS)
                }
                is DataState.ErrorThrowable -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                    Action.showSnackBar(
                        binding.container,
                        state.throwable.message,
                        Snackbar.LENGTH_LONG
                    )
                    viewModel.setViewState(ViewState.ERROR)
                }
            }
        }
    }

    private fun getUid(): String? {
        var uid: String? = null
        viewModel.uid.observe(viewLifecycleOwner) {
            if (it != null) uid = it
        }
        return uid
    }

}
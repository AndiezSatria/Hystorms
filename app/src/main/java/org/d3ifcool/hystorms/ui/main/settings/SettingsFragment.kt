package org.d3ifcool.hystorms.ui.main.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.databinding.FragmentSettingsBinding
import org.d3ifcool.hystorms.viewmodel.SettingViewModel

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            settingViewModel = viewModel

            viewModel.viewState.observe(viewLifecycleOwner) {
                layoutData.viewState = it
                settingsButton.viewState = it
            }
        }
        observeMessage()
        observeUid()
        observeUser()
    }

    private fun observeUser() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) binding.layoutData.user = user
        }
    }

    private fun observeMessage() {
        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Action.showSnackBar(binding.coordinator, message, Snackbar.LENGTH_LONG)
                viewModel.doneShowMessage()
            }
        }
    }

    private fun observeUid() {
        viewModel.uid.observe(viewLifecycleOwner) { uid ->
            if (uid != null) viewModel.getUser(uid)
        }
    }
}
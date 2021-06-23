package org.d3ifcool.hystorms.ui.main.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.databinding.FragmentSettingsBinding
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.ui.auth.AuthActivity
import org.d3ifcool.hystorms.ui.main.MainFragmentDirections
import org.d3ifcool.hystorms.viewmodel.SettingViewModel

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)
        arguments = requireActivity().intent.extras

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            settingViewModel = viewModel

            viewModel.viewState.observe(viewLifecycleOwner) {
                layoutData.viewState = it
                settingsButton.viewState = it
            }
            settingsButton.editProfile.setOnClickListener {
                Navigation.findNavController(requireActivity(), R.id.nav_main).navigate(
                    MainFragmentDirections.actionMainFragmentToEditProfileFragment(getUser())
                )
            }
            settingsButton.changePassword.setOnClickListener {
                Navigation.findNavController(requireActivity(), R.id.nav_main).navigate(
                    MainFragmentDirections.actionMainFragmentToChangePasswordFragment()
                )
            }
            settingsButton.help.setOnClickListener {
                Navigation.findNavController(requireActivity(), R.id.nav_main).navigate(
                    MainFragmentDirections.actionMainFragmentToHelpFragment()
                )
            }
            settingsButton.info.setOnClickListener {
                Navigation.findNavController(requireActivity(), R.id.nav_main).navigate(
                    MainFragmentDirections.actionMainFragmentToAppInfoFragment()
                )
            }
            settingsButton.notification.setOnClickListener {
                Navigation.findNavController(requireActivity(), R.id.nav_main).navigate(
                    MainFragmentDirections.actionMainFragmentToNoitificationSettingFragment()
                )
            }
            settingsButton.logout.setOnClickListener {
                Action.showToast(requireContext(), "Berhasil logout")
                viewModel.signOut()
            }

            settingsButton.exit.setOnClickListener {
                requireActivity().finishAndRemoveTask()
            }
        }
        observeMessage()
        observeUid()
        observeUser()
        observeIsLoggedOut()
    }

    private fun observeIsLoggedOut() {
        viewModel.isLoggedOut.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut) {
                val intent = Intent(requireActivity(), AuthActivity::class.java)
                startActivity(intent)
                viewModel.doneLoggedOut()
                requireActivity().finish()
            }
        }
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

    private fun getUser(): User? {
        var user: User? = null
        viewModel.user.observe(viewLifecycleOwner) {
            if (it != null) user = it
        }
        return user
    }

    private fun observeUid() {
        viewModel.uid.observe(viewLifecycleOwner) { uid ->
            if (uid != null) {
                Action.showLog(uid)
                viewModel.getUser(uid)
            }
        }
    }
}
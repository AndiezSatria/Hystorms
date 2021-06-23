package org.d3ifcool.hystorms.ui.main.settings

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
import org.d3ifcool.hystorms.databinding.FragmentChangePasswordBinding
import org.d3ifcool.hystorms.viewmodel.ChangePasswordViewModel

@AndroidEntryPoint
class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {
    private lateinit var binding: FragmentChangePasswordBinding
    private val viewModel: ChangePasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChangePasswordBinding.bind(view)
        val navHostFragment = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navHostFragment.graph)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            changePassViewModel = viewModel
            appBar.toolbar.setupWithNavController(navHostFragment, appBarConfiguration)

            btnSend.setOnClickListener {
                val pass = getPass()
                if (pass != null) {
                    viewModel.changePassword(pass)
                }
            }
        }
        observeMessage()
        observeMessageUpdate()
    }

    private fun observeMessage() {
        viewModel.message.observe(viewLifecycleOwner) {
            if (it != null) {
                Action.showSnackBar(binding.coordinator, it, Snackbar.LENGTH_LONG)
                viewModel.doneShowMessage()
            }
        }
    }

    private fun observeMessageUpdate() {
        viewModel.messageUpdate.observe(viewLifecycleOwner) {
            if (it != null) {
                Action.showSnackBar(binding.coordinator, it, Snackbar.LENGTH_LONG)
                viewModel.doneShowMessageUpdate()
                findNavController().navigateUp()
            }
        }
    }

    private fun getPass(): String? {
        if (binding.tfPassword.editText?.text.toString()
                .trim() == "" || binding.tfConfirmPass.editText?.text.toString().trim() == ""
        ) {
            Action.showSnackBar(
                binding.coordinator,
                "Semua bagan harus diisi.",
                Snackbar.LENGTH_LONG
            )
            return null
        }
        if (binding.tfPassword.editText?.text.toString().length < 6 || binding.tfConfirmPass.editText?.text.toString().length < 6) {
            Action.showSnackBar(
                binding.coordinator,
                "Password harus lebih dari 6 digit.",
                Snackbar.LENGTH_LONG
            )
            return null
        }
        if (binding.tfPassword.editText?.text.toString() != binding.tfConfirmPass.editText?.text.toString()) {
            Action.showSnackBar(
                binding.coordinator,
                "Password dan password ulang harus sama.",
                Snackbar.LENGTH_LONG
            )
            return null
        }
        return binding.tfPassword.editText?.text.toString()
    }
}
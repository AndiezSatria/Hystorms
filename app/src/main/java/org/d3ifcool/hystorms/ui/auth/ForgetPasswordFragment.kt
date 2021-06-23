package org.d3ifcool.hystorms.ui.auth

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
import org.d3ifcool.hystorms.databinding.FragmentForgetPasswordBinding
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.util.ViewState
import org.d3ifcool.hystorms.viewmodel.ForgotPasswordViewModel

@AndroidEntryPoint
class ForgetPasswordFragment : Fragment(R.layout.fragment_forget_password) {

    private lateinit var binding: FragmentForgetPasswordBinding

    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentForgetPasswordBinding.bind(view)

        val navHostFragment = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navHostFragment.graph)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = forgotPasswordViewModel

            appBar.toolbar.setupWithNavController(navHostFragment, appBarConfiguration)
            btnSend.setOnClickListener {
                if (checkInput()) {
                    val email = getEmail()
                    forgotPasswordViewModel.resetPass(email)
                }
            }
        }

        observeResult()
    }

    private fun observeResult() {
        forgotPasswordViewModel.resetPassResult.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Canceled -> {
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                    forgotPasswordViewModel.setState(ViewState.ERROR)
                }
                is DataState.Error -> {
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.localizedMessage,
                        Snackbar.LENGTH_LONG
                    )
                    forgotPasswordViewModel.setState(ViewState.ERROR)
                }
                is DataState.Loading -> {
                    forgotPasswordViewModel.setState(ViewState.LOADING)
                }
                is DataState.Success -> {
                    Action.showSnackBar(
                        binding.coordinator,
                        state.data,
                        Snackbar.LENGTH_LONG
                    )
                    findNavController().navigateUp()
                }
                is DataState.ErrorThrowable -> {
                    forgotPasswordViewModel.setState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.coordinator,
                        state.throwable.localizedMessage,
                        Snackbar.LENGTH_LONG
                    )
                }
            }
        }
    }

    private fun checkInput(): Boolean {
        return if (binding.tfEmail.editText?.text.toString().trim() == "") {
//            Action.showDialog(
//                "Peringatan",
//                "Mohon isi semua bagan!",
//                requireContext(),
//                confirmText = "Ok",
//                confirmListener = {
//                    it.dismissWithAnimation()
//                })
            Action.showSnackBar(
                binding.coordinator,
                "Mohon isi semua bagan!",
                Snackbar.LENGTH_SHORT
            )
            false
        } else true
    }

    private fun getEmail(): String {
        return binding.tfEmail.editText?.text.toString()
    }
}
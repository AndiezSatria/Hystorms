package org.d3ifcool.hystorms.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.databinding.FragmentLoginBinding
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.ui.main.MainActivity
import org.d3ifcool.hystorms.util.ViewState
import org.d3ifcool.hystorms.viewmodel.LoginViewModel

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        val navHostFragment = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navHostFragment.graph)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = loginViewModel

            appBar.toolbar.setupWithNavController(navHostFragment, appBarConfiguration)
            btnForgetPass.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgetPasswordFragment())
            }
            btnLogin.setOnClickListener {
                if (checkInput()) {
                    loginViewModel.signIn(getEmail(), getPass())
                }
            }
        }
        observeSignInUid()
        observeLoggedInUser()
    }

    private fun observeSignInUid() {
        loginViewModel.authenticatedUid.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Canceled -> {
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                    loginViewModel.setState(ViewState.ERROR)
                }
                is DataState.Error -> {
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.localizedMessage,
                        Snackbar.LENGTH_LONG
                    )
                    loginViewModel.setState(ViewState.ERROR)
                }
                is DataState.Loading -> {
                    loginViewModel.setState(ViewState.LOADING)
                }
                is DataState.Success -> {
                    val uid = state.data
                    loginViewModel.getUser(uid, getToken())
                }
                is DataState.ErrorThrowable -> {
                    Action.showSnackBar(
                        binding.coordinator,
                        state.throwable.localizedMessage,
                        Snackbar.LENGTH_LONG
                    )
                    loginViewModel.setState(ViewState.ERROR)
                }
            }
        }
    }

    private fun getToken(): String {
        var token = ""
        loginViewModel.token.observe(viewLifecycleOwner) {
            if (it != null) token = it
        }
        return token
    }

    private fun observeLoggedInUser() {
        loginViewModel.loggedInUser.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Canceled -> {
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.message,
                        Snackbar.LENGTH_LONG
                    )
                    loginViewModel.setState(ViewState.ERROR)
                }
                is DataState.Error -> {
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.localizedMessage,
                        Snackbar.LENGTH_LONG
                    )
                    loginViewModel.setState(ViewState.ERROR)
                }
                is DataState.Loading -> {
                }
                is DataState.Success -> {
                    val user = state.data
//                    Action.showSnackBar(
//                        binding.coordinator,
//                        "Berhasil masuk. Selamat datang ${user.name}.",
//                        Snackbar.LENGTH_LONG
//                    )
                    Action.showToast(
                        requireContext(),
                        "Berhasil masuk. Selamat datang ${user.name}."
                    )
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    intent.putExtra(Constant.USER, user.uid)
                    startActivity(intent)
                    requireActivity().finish()
                }
                is DataState.ErrorThrowable -> {
                    loginViewModel.setState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.coordinator,
                        state.throwable.localizedMessage,
                        Snackbar.LENGTH_LONG
                    )
                }
            }
        }
    }

    private fun getEmail(): String {
        return binding.tfEmail.editText?.text.toString()
    }

    private fun getPass(): String {
        return binding.tfPassword.editText?.text.toString()
    }

    private fun checkInput(): Boolean {
        return if (binding.tfEmail.editText?.text.toString()
                .trim() == "" || binding.tfPassword.editText?.text.toString().trim() == ""
        ) {
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
        } else if (binding.tfPassword.editText?.text.toString().length < 6) {
//            Action.showDialog(
//                "Peringatan",
//                "Kata sandi kurang dari 6 karakter!",
//                requireContext(),
//                confirmText = "Ok",
//                confirmListener = {
//                    it.dismissWithAnimation()
//                })
            Action.showSnackBar(
                binding.coordinator,
                "Kata sandi kurang dari 6 karakter!",
                Snackbar.LENGTH_SHORT
            )
            false
        } else true
    }
}
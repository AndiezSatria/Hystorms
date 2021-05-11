package org.d3ifcool.hystorms.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.data.Constant
import org.d3ifcool.hystorms.databinding.FragmentLoginBinding
import org.d3ifcool.hystorms.ui.main.MainActivity
import org.d3ifcool.hystorms.viewmodel.LoginViewModel

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = loginViewModel
        binding.apply {
            binding.btnForgetPass.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgetPasswordFragment())
            }
            binding.btnLogin.setOnClickListener {
                if (checkInput()) {
                    loginViewModel.signIn(getEmail(), getPass())
                }
            }
        }
        observeSignInUid()
        observeLoggedInUser()
    }

    private fun observeSignInUid() {
        loginViewModel.authenticatedUid.observe(viewLifecycleOwner) { dataOrException ->
            if (dataOrException.data != null) {
                val uid = dataOrException.data!!
                loginViewModel.getUser(uid)
            }
            if (dataOrException.exception != null) {
                dataOrException.exception?.message?.let { message ->
                    showErrorDialog("Error", message, requireContext()) {
                        it.dismissWithAnimation()
                    }
                }
            }
        }
    }

    private fun observeLoggedInUser() {
        loginViewModel.loggedInUser.observe(viewLifecycleOwner) { dataOrException ->
            if (dataOrException.data != null) {
                val user = dataOrException.data!!
                showSnackBar("Berhasil login. Selamat datang ${user.name}")
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }
            if (dataOrException.exception != null) {
                dataOrException.exception?.message?.let { message ->
                    showErrorDialog("Error", message, requireContext()) {
                        it.dismissWithAnimation()
                    }
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
            showErrorDialog("Peringatan", "Mohon isi semua bidang", requireContext()) {
                it.dismissWithAnimation()
            }
            false
        } else if (binding.tfPassword.editText?.text.toString().length < 6) {
            showErrorDialog("Peringatan", "Password kurang dari 6 karakter", requireContext()) {
                it.dismissWithAnimation()
            }
            false
        } else true
    }

    private fun showSnackBar(
        content: String
    ) {
        Log.d(Constant.APP_DEBUG, "Snackbar Kepanggil")
        val snackbar = Snackbar.make(binding.coordinator, content, Snackbar.LENGTH_INDEFINITE)
        snackbar.show()
    }

    private fun showErrorDialog(
        title: String,
        desc: String,
        context: Context,
        listener: SweetAlertDialog.OnSweetClickListener
    ) {
        SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            .setTitleText(title)
            .setContentText(desc)
            .setConfirmButton("OK", listener)
            .show()
    }
}
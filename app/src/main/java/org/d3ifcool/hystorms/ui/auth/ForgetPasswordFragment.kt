package org.d3ifcool.hystorms.ui.auth

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.data.Constant
import org.d3ifcool.hystorms.databinding.FragmentForgetPasswordBinding
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
        forgotPasswordViewModel.resetPassResult.observe(viewLifecycleOwner) { dataOrException ->
            if (dataOrException.data != null) {
                showSnackBar("Email berhasil dikirim ke alamat email.")
                findNavController().navigate(ForgetPasswordFragmentDirections.actionForgetPasswordFragmentToLoginFragment())
            }
            if (dataOrException.exception != null) {
                dataOrException.exception?.message?.let { message ->
                    showErrorDialog("Error", message, requireContext()) {
                        it.dismiss()
                    }
                }
            }
        }
    }

    private fun checkInput(): Boolean {
        return if (binding.tfEmail.editText?.text.toString().trim() == "") {
            showErrorDialog("Peringatan", "Mohon isi semua bidang", requireContext()) {
                it.dismissWithAnimation()
            }
            false
        } else true
    }

    private fun getEmail(): String {
        return binding.tfEmail.editText?.text.toString()
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
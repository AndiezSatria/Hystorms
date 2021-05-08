package org.d3ifcool.hystorms.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.data.Constant
import org.d3ifcool.hystorms.databinding.FragmentRegisterBinding
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.util.ButtonUploadState
import org.d3ifcool.hystorms.viewmodel.RegisterViewModel
import java.io.File

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = registerViewModel

        val navHostFragment = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navHostFragment.graph)

        binding.apply {
            appBar.toolbar.setupWithNavController(navHostFragment, appBarConfiguration)
            registerViewModel.buttonState.observe(viewLifecycleOwner) {
                if (it == ButtonUploadState.ADD) {
                    fabPicture.setOnClickListener {
                        ImagePicker.with(this@RegisterFragment)
                            .cropSquare()
                            .maxResultSize(512, 512)
                            .start()
                    }
                } else if (it == ButtonUploadState.DELETE) {
                    fabPicture.setOnClickListener {
                        registerViewModel.deleteFile()
                        registerViewModel.setButtonState(ButtonUploadState.ADD)
                    }
                }
            }
            registerViewModel.dataRegister.observe(viewLifecycleOwner) { data ->
                if (data != null) {
                    if (data.data != null) {
                        data.data?.let {
                            showSnackBar("Akun ${it.name} sudah dibuat") {
                                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
                                viewModel.deleteFile()
                                viewModel.deleteData()
                            }
                        }
                    } else {
                        data.exception?.message?.let {
                            showErrorDialog(it, requireContext()) { alert ->
                                alert.dismissWithAnimation()
                            }
                        }
                    }
                } else {
                    btnRegister.setOnClickListener {
                        val user: User? = getUser()
                        val pass: String = getPass()
                        if (user != null && pass != "") {
                            var fileToUpload: File? = null
                            registerViewModel.fileToUpload.observe(viewLifecycleOwner) { file ->
                                fileToUpload = file
                            }
                            registerViewModel.register(user, pass, fileToUpload)
                        }
                    }
                }
            }
        }
    }

    private fun getPass(): String {
        var pass = ""
        if (checkPassword()) {
            pass = binding.tfPassword.editText.toString()
        }
        return pass
    }

    private fun checkPassword(): Boolean {
        return if (binding.tfPassword.editText.toString() == binding.tfConfirmPass.editText.toString()) true
        else {
            showErrorDialog(
                "Password dan Password ulang tidak sama!",
                requireContext()
            ) { it.dismissWithAnimation() }
            false
        }
    }

    private fun getUser(): User? {
        return if (!checkEditTextEmptiness()) null
        else User(
            "",
            binding.tfName.editText?.text.toString(),
            binding.tfEmail.editText?.text.toString(),
        )
    }

    private fun checkEditTextEmptiness(): Boolean {
        var condition = false
        if (binding.tfName.editText?.text.toString().trim() == "" &&
            binding.tfEmail.editText?.text.toString().trim() == "" &&
            binding.tfPassword.editText?.text.toString().trim() == "" &&
            binding.tfConfirmPass.editText?.text.toString().trim() == ""
        ) {
            showErrorDialog("Mohon isi semua bidang", requireContext()) {
                it.dismissWithAnimation()
            }
        } else {
            condition = true
        }
        return condition
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val file: File = ImagePicker.getFile(data)!!
                registerViewModel.setFile(file)
                registerViewModel.setButtonState(ButtonUploadState.DELETE)
                Log.d(Constant.APP_DEBUG, "Sukses")
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(
                    requireContext(),
                    ImagePicker.getError(data),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                Toast.makeText(
                    requireContext(),
                    "Task Cancelled",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showSnackBar(
        content: String, listener: View.OnClickListener
    ) {
        val snackbar = Snackbar.make(binding.coordinator, content, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(R.string.text_login, listener)
        snackbar.show()
    }

    private fun showErrorDialog(
        desc: String,
        context: Context,
        listener: SweetAlertDialog.OnSweetClickListener
    ) {
        SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Error")
            .setContentText(desc)
            .setNeutralText("Ok")
            .setNeutralClickListener(listener)
    }
}
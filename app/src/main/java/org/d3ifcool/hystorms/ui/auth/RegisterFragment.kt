package org.d3ifcool.hystorms.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import java.util.regex.Matcher
import java.util.regex.Pattern


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
            btnRegister.setOnClickListener {
                val user: User? = getUser()
                Log.d(Constant.APP_DEBUG, "Kepencet")
                if (user != null) {
                    val pass: String = getPass()
                    if (pass != "") {
                        registerViewModel.registerAuth(user, pass)
                    }
                } else {
                    Log.d(Constant.APP_DEBUG, "Gak jalan")
                }
            }
        }
        listenToSavedProfileUser()
        listenToUploadedProfileUser()
        listenToAuthenticatedUser()
    }

    private fun listenToAuthenticatedUser() {
        registerViewModel.authenticatedUser.observe(viewLifecycleOwner,
            { dataOrException ->
                if (dataOrException.data != null) {
                    var content = "Autentikasi akun selesai."
                    val user: User = dataOrException.data!!
                    val file: File? = getFileToUpload()
                    if (file != null) {
                        content += " Mengunggah foto profil."
                        registerViewModel.uploadProfile(user, file)
                    } else {
                        content += " Menyimpan data akun."
                        registerViewModel.saveUser(user)
                    }
                    showSnackBar(content, null)
                }
                if (dataOrException.exception != null) {
                    dataOrException.exception?.message?.let {
                        Log.e(Constant.APP_DEBUG, it)
                        showErrorDialog(
                            "Error",
                            it,
                            requireContext()
                        ) { alert ->
                            alert.dismissWithAnimation()
                        }
                    }
                }
            })
    }

    private fun listenToUploadedProfileUser() {
        registerViewModel.profileUploadedUser.observe(viewLifecycleOwner) { dataOrException ->
            if (dataOrException.data != null) {
                val content = "Foto profil berhasil diunggah. Menyimpan data akun."
                val user: User = dataOrException.data!!
                registerViewModel.saveUser(user)
                showSnackBar(content, null)
            }
            if (dataOrException.exception != null) {
                dataOrException.exception?.message?.let {
                    Log.e(Constant.APP_DEBUG, it)
                    showErrorDialog(
                        "Error",
                        it,
                        requireContext()
                    ) { alert ->
                        alert.dismissWithAnimation()
                    }
                }
            }
        }
    }

    private fun listenToSavedProfileUser() {
        registerViewModel.savedUser.observe(viewLifecycleOwner) { dataOrException ->
            if (dataOrException.data != null) {
                val content = "Akun berhasil disimpan. Silahkan masuk dengan akun Anda."
                showSnackBar(content) {
                    findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
                    registerViewModel.resetData()
                }
            }
            if (dataOrException.exception != null) {
                dataOrException.exception?.message?.let {
                    Log.e(Constant.APP_DEBUG, it)
                    showErrorDialog(
                        "Error",
                        it,
                        requireContext()
                    ) { alert ->
                        alert.dismissWithAnimation()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        registerViewModel.resetData()
    }

    private fun getPass(): String {
        var pass = ""
        if (checkPassword()) {
            pass = binding.tfPassword.editText?.text.toString()
        }
        return pass
    }

    private fun getFileToUpload(): File? {
        var fileToUpload: File? = null
        registerViewModel.fileToUpload.observe(viewLifecycleOwner) { file ->
            fileToUpload = file
        }
        return fileToUpload
    }

    private fun checkPassword(): Boolean {
        return when {
            binding.tfPassword.editText?.text.toString()
                .trim() != binding.tfConfirmPass.editText?.text.toString().trim() -> {
                showErrorDialog(
                    "Perhatian",
                    "Password dan Password ulang tidak sama!",
                    requireContext()
                ) { it.dismissWithAnimation() }
                false
            }
            binding.tfPassword.editText?.text.toString().length < 6 -> {
                showErrorDialog(
                    "Perhatian",
                    "Password kurang dari 6 karakter",
                    requireContext()
                ) { it.dismissWithAnimation() }
                false
            }
            else -> true
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
            showErrorDialog("Perhatian", "Mohon isi semua bidang", requireActivity()) {
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
        content: String, listener: View.OnClickListener?
    ) {
        Log.d(Constant.APP_DEBUG, "Snackbar Kepanggil")
        val snackbar = Snackbar.make(binding.coordinator, content, Snackbar.LENGTH_INDEFINITE)
        if (listener != null) snackbar.setAction(R.string.text_login, listener)
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
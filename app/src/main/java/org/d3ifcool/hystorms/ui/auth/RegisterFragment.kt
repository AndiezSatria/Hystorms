package org.d3ifcool.hystorms.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.constant.Constant
import org.d3ifcool.hystorms.databinding.FragmentRegisterBinding
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.state.DataState
import org.d3ifcool.hystorms.ui.main.MainActivity
import org.d3ifcool.hystorms.util.ButtonUploadState
import org.d3ifcool.hystorms.util.EspressoIdlingResource
import org.d3ifcool.hystorms.util.ViewState
import org.d3ifcool.hystorms.viewmodel.RegisterViewModelNew
import java.io.File

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val registerViewModel: RegisterViewModelNew by viewModels()

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
                if (user != null) {
                    val pass: String = getPass()
                    if (pass != "") {
                        registerViewModel.registerAuth(user, pass)
                    }
                }
            }
        }
        listenToSavedProfileUser()
        listenToUploadedProfileUser()
        listenToAuthenticatedUser()
    }

    private fun listenToAuthenticatedUser() {
        registerViewModel.authenticatedUser.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Canceled -> {
                    EspressoIdlingResource.decrement()
                    registerViewModel.setViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.localizedMessage,
                        Snackbar.LENGTH_LONG
                    )
                }
                is DataState.Error -> {
                    EspressoIdlingResource.decrement()
                    registerViewModel.setViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.localizedMessage,
                        Snackbar.LENGTH_LONG
                    )
                }
                is DataState.Loading -> {
                    EspressoIdlingResource.increment()
                    registerViewModel.setViewState(ViewState.LOADING)
                }
                is DataState.Success<User> -> {
                    var content = "Autentikasi akun selesai."
                    val user = state.data
                    val file = getFileToUpload()
                    if (file != null) {
                        content += " Mengunggah foto profil"
                        registerViewModel.uploadProfile(user, file)
                    } else {
                        content += " Menyimpan data akun."
                        registerViewModel.saveUser(user)
                    }
                    Action.showSnackBar(binding.coordinator, content, Snackbar.LENGTH_INDEFINITE)
                }
                is DataState.ErrorThrowable -> {
                    registerViewModel.setViewState(ViewState.ERROR)
                    EspressoIdlingResource.decrement()
                    Action.showSnackBar(
                        binding.coordinator,
                        state.throwable.localizedMessage,
                        Snackbar.LENGTH_LONG
                    )
                }
            }
        }
    }

    private fun listenToUploadedProfileUser() {
        registerViewModel.profileUploadedUser.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Canceled -> {
                    EspressoIdlingResource.decrement()
                    registerViewModel.setViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.localizedMessage,
                        Snackbar.LENGTH_LONG
                    )
                }
                is DataState.Error -> {
                    EspressoIdlingResource.decrement()
                    registerViewModel.setViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.localizedMessage,
                        Snackbar.LENGTH_LONG
                    )
                }
                is DataState.Loading -> {
                }
                is DataState.Success<User> -> {
                    val content = "Foto profil berhasil diunggah. Menyimpan data akun."
                    val user: User = state.data
                    registerViewModel.saveUser(user)
                    Action.showSnackBar(binding.coordinator, content, Snackbar.LENGTH_INDEFINITE)
                }
                is DataState.ErrorThrowable -> {
                    EspressoIdlingResource.decrement()
                    registerViewModel.setViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.coordinator,
                        state.throwable.localizedMessage,
                        Snackbar.LENGTH_LONG
                    )
                }
            }
        }
    }

    private fun listenToSavedProfileUser() {
        registerViewModel.savedUser.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Canceled -> {
                    EspressoIdlingResource.decrement()
                    registerViewModel.setViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.localizedMessage,
                        Snackbar.LENGTH_LONG
                    )
                }
                is DataState.Error -> {
                    EspressoIdlingResource.decrement()
                    registerViewModel.setViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.coordinator,
                        state.exception.localizedMessage,
                        Snackbar.LENGTH_LONG
                    )
                }
                is DataState.Loading -> {
                }
                is DataState.Success<User> -> {
                    EspressoIdlingResource.decrement()
                    registerViewModel.setViewState(ViewState.SUCCESS)
                    val user = state.data
                    val content = "Selamat Datang"
//                    Action.showSnackBar(
//                        binding.coordinator,
//                        content,
//                        Snackbar.LENGTH_INDEFINITE
//                    )
                    Action.showToast(requireContext(), content)
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    intent.putExtra(Constant.USER, user.uid)
                    startActivity(intent)
                    requireActivity().finish()
                }
                is DataState.ErrorThrowable -> {
                    EspressoIdlingResource.decrement()
                    registerViewModel.setViewState(ViewState.ERROR)
                    Action.showSnackBar(
                        binding.coordinator,
                        state.throwable.localizedMessage,
                        Snackbar.LENGTH_LONG
                    )
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
                Action.showSnackBar(
                    binding.coordinator,
                    "Password dan password ulang tidak sama!",
                    Snackbar.LENGTH_SHORT
                )

                false
            }
            binding.tfPassword.editText?.text.toString().length < 6 -> {
                Action.showSnackBar(
                    binding.coordinator,
                    "Password kurang dari 6 karakter!",
                    Snackbar.LENGTH_SHORT
                )
                false
            }
            else -> true
        }
    }

    private fun getUser(): User? {
        return if (!checkEditTextEmptiness()) null
        else {
            val user = User(
                "",
                binding.tfName.editText?.text.toString(),
                binding.tfEmail.editText?.text.toString(),
            )
            registerViewModel.token.observe(viewLifecycleOwner) {
                if (it != null) {
                    user.token = it
                }
            }
            user
        }
    }

    private fun checkEditTextEmptiness(): Boolean {
        var condition = false
        if (binding.tfName.editText?.text.toString().trim() == "" &&
            binding.tfEmail.editText?.text.toString().trim() == "" &&
            binding.tfPassword.editText?.text.toString().trim() == "" &&
            binding.tfConfirmPass.editText?.text.toString().trim() == ""
        ) {
//            Action.showDialog(
//                "Perhatian",
//                "Mohon isi semua bagan!",
//                requireContext(),
//                confirmListener = {
//                    it.dismissWithAnimation()
//                })
            Action.showSnackBar(
                binding.coordinator,
                "Mohon isi semua bagan!",
                Snackbar.LENGTH_SHORT
            )
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
            }
            ImagePicker.RESULT_ERROR -> {
                Action.showToast(
                    requireContext(),
                    ImagePicker.getError(data), Toast.LENGTH_LONG
                )
            }
            else -> {
                Action.showToast(requireContext(), "Tugas dibatalkan")
            }
        }
    }
}